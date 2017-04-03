package org.rainbow.security.core.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import org.rainbow.security.core.authentication.PasswordAttemptUpdater;
import org.rainbow.security.core.persistence.exceptions.ApplicationNotFoundException;
import org.rainbow.security.core.persistence.exceptions.MembershipNotFoundException;
import org.rainbow.security.core.utilities.DateHelper;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 
 * @author Biya-Bi
 */
public class PasswordAttemptUpdaterImpl extends JdbcDaoSupport implements PasswordAttemptUpdater {

	private final PasswordEncoder passwordEncoder;
	private final String applicationName;

	private final String selectQuery = "SELECT m.PASSWORD,m.FAILED_PWD_ATMPT_WIN_START,m.FAILED_PWD_ATMPT_CNT, pp.PASSWORD_ATTEMPT_WINDOW, pp.MAXIMUM_INVALID_PASSWORD_ATTEMPTS "
			+ "FROM MEMBERSHIPS m INNER JOIN USERS u ON m.USER_ID=u.ID INNER JOIN APPLICATIONS a "
			+ "ON m.APPLICATION_ID=a.ID INNER JOIN PASSWORD_POLICIES pp ON a.ID=pp.APPLICATION_ID "
			+ "WHERE u.USER_NAME=? AND a.NAME=?";

	private final String failedPasswordAttemptUpdateQuery = "UPDATE MEMBERSHIPS SET FAILED_PWD_ATMPT_WIN_START = ?, FAILED_PWD_ATMPT_CNT=? WHERE "
			+ "USER_ID=(SELECT u.ID FROM USERS u INNER JOIN APPLICATIONS a ON u.APPLICATION_ID=a.ID WHERE "
			+ "u.USER_NAME =? AND a.NAME=?)";

	private final String failedPasswordAttemptCountUpdateQuery = "UPDATE MEMBERSHIPS SET FAILED_PWD_ATMPT_CNT=? WHERE "
			+ "USER_ID=(SELECT u.ID FROM USERS u INNER JOIN APPLICATIONS a ON u.APPLICATION_ID=a.ID WHERE "
			+ "u.USER_NAME =? AND a.NAME=?)";

	private final String lockUpdateQuery = "UPDATE MEMBERSHIPS SET LOCKED_OUT=?, LAST_LOCK_OUT_DATE=? WHERE "
			+ "USER_ID=(SELECT u.ID FROM USERS u INNER JOIN APPLICATIONS a ON u.APPLICATION_ID=a.ID WHERE "
			+ "u.USER_NAME =? AND a.NAME=?)";

	private final String lastLoginDateUpdateQuery = "UPDATE MEMBERSHIPS SET LAST_LOGIN_DATE=? WHERE "
			+ "USER_ID=(SELECT u.ID FROM USERS u INNER JOIN APPLICATIONS a ON u.APPLICATION_ID=a.ID WHERE "
			+ "u.USER_NAME =? AND a.NAME=?)";

	private final String userUpdateQuery = "UPDATE USERS SET LAST_ACTIVITY_DATE=?, LAST_UPDATE_DATE=?, UPDATER=? WHERE USER_NAME=? AND "
			+ "APPLICATION_ID=(SELECT a.ID FROM APPLICATIONS a WHERE a.NAME=?)";

	private final String countApplicationsQuery = "SELECT COUNT(ID) FROM applications WHERE name=?";
	private final String countMembershipsQuery = "SELECT COUNT(m.USER_ID) FROM memberships m INNER JOIN users u ON m.USER_ID=u.ID INNER JOIN applications a on m.APPLICATION_ID=a.ID WHERE u.USER_NAME=? AND a.name=?";

	public PasswordAttemptUpdaterImpl(PasswordEncoder passwordEncoder, String applicationName) {
		super();
		if (passwordEncoder == null)
			throw new IllegalArgumentException("The passwordEncoder argument cannot be null.");
		if (applicationName == null)
			throw new IllegalArgumentException("The applicationName argument cannot be null.");
		this.passwordEncoder = passwordEncoder;
		this.applicationName = applicationName;
	}

	private boolean applicationExists() {
		return getJdbcTemplate().queryForObject(countApplicationsQuery, new String[] { applicationName },
				new RowMapper<Boolean>() {
					@Override
					public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getInt(1) == 1;
					}
				});
	}

	private boolean membershipExists(String userName) {
		return getJdbcTemplate().queryForObject(countMembershipsQuery, new String[] { userName, applicationName },
				new RowMapper<Boolean>() {
					@Override
					public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getInt(1) == 1;
					}
				});
	}

	@Override
	public void update(String userName, String password) {
		if (!applicationExists())
			throw new ApplicationNotFoundException(applicationName);
		if (!membershipExists(userName))
			throw new MembershipNotFoundException(userName, applicationName);
		getJdbcTemplate().query(selectQuery, new String[] { userName, applicationName }, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				final String encodedPassword = rs.getString("PASSWORD");
				final Timestamp failedPasswordAttemptWindowStart = rs.getTimestamp("FAILED_PWD_ATMPT_WIN_START");
				final int failedPasswordAttemptCount = rs.getInt("FAILED_PWD_ATMPT_CNT");
				final int passwordAttemptWindow = rs.getInt("PASSWORD_ATTEMPT_WINDOW");
				final int maximumInvalidPasswordAttempts = rs.getInt("MAXIMUM_INVALID_PASSWORD_ATTEMPTS");

				Timestamp now = new Timestamp(Calendar.getInstance().getTimeInMillis());
				Calendar c = Calendar.getInstance();

				if (!passwordEncoder.matches(password, encodedPassword)) {
					c.setTime(failedPasswordAttemptWindowStart);
					c.add(Calendar.MINUTE, passwordAttemptWindow);
					if (now.getTime() > c.getTimeInMillis()) {
						getJdbcTemplate().update(new PreparedStatementCreator() {
							@Override
							public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
								PreparedStatement preparedStatement = con
										.prepareStatement(failedPasswordAttemptUpdateQuery);
								preparedStatement.setTimestamp(1, now);
								preparedStatement.setInt(2, 1);
								preparedStatement.setString(3, userName);
								preparedStatement.setString(4, applicationName);
								return preparedStatement;
							}
						});
					} else {

						getJdbcTemplate().update(new PreparedStatementCreator() {
							@Override
							public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
								PreparedStatement preparedStatement = con
										.prepareStatement(failedPasswordAttemptCountUpdateQuery);
								preparedStatement.setInt(1, failedPasswordAttemptCount + 1);
								preparedStatement.setString(2, userName);
								preparedStatement.setString(3, applicationName);
								return preparedStatement;
							}
						});
					}
					if (failedPasswordAttemptCount >= maximumInvalidPasswordAttempts) {
						getJdbcTemplate().update(new PreparedStatementCreator() {
							@Override
							public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
								PreparedStatement preparedStatement = con.prepareStatement(lockUpdateQuery);
								preparedStatement.setBoolean(1, true);
								preparedStatement.setTimestamp(2, now);
								preparedStatement.setString(3, userName);
								preparedStatement.setString(4, applicationName);
								return preparedStatement;
							}
						});
					}
				} else {
					if (failedPasswordAttemptCount > 0) {
						c.setTime(DateHelper.toDate("1754-01-01"));
						getJdbcTemplate().update(new PreparedStatementCreator() {
							@Override
							public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
								PreparedStatement preparedStatement = con
										.prepareStatement(failedPasswordAttemptUpdateQuery);
								preparedStatement.setDate(1, new java.sql.Date(c.getTimeInMillis()));
								preparedStatement.setInt(2, 0);
								preparedStatement.setString(3, userName);
								preparedStatement.setString(4, applicationName);
								return preparedStatement;
							}
						});
					}
					getJdbcTemplate().update(new PreparedStatementCreator() {
						@Override
						public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
							PreparedStatement preparedStatement = con.prepareStatement(lastLoginDateUpdateQuery);
							preparedStatement.setTimestamp(1, now);
							preparedStatement.setString(2, userName);
							preparedStatement.setString(3, applicationName);
							return preparedStatement;
						}
					});
				}

				getJdbcTemplate().update(new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						PreparedStatement preparedStatement = con.prepareStatement(userUpdateQuery);
						preparedStatement.setTimestamp(1, now);
						preparedStatement.setTimestamp(2, now);
						preparedStatement.setString(3, userName);
						preparedStatement.setString(4, userName);
						preparedStatement.setString(5, applicationName);
						return preparedStatement;
					}
				});
			}
		});
	}

}