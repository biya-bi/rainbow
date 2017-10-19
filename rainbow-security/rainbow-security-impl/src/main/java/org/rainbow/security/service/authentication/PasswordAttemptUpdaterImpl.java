package org.rainbow.security.service.authentication;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.rainbow.security.service.exceptions.ApplicationNotFoundException;
import org.rainbow.security.service.exceptions.CredentialsNotFoundException;
import org.rainbow.security.utilities.DateUtil;
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

	private PasswordEncoder passwordEncoder;
	private String applicationName;

	private static final String SELECT_QUERY = "SELECT m.USER_ID,m.ID AS MEMBERSHIP_ID,m.PASSWORD,m.FAILED_PWD_ATMPT_WIN_START,m.FAILED_PWD_ATMPT_CNT, lp.ATTEMPT_WINDOW, lp.THRESHOLD AS LOCK_OUT_THRESHOLD, lgp.THRESHOLD AS LOGIN_HISTORY_THRESHOLD "
			+ "FROM MEMBERSHIPS m INNER JOIN USERS u ON m.USER_ID=u.ID INNER JOIN APPLICATIONS a "
			+ "ON u.APPLICATION_ID=a.ID INNER JOIN PASSWORD_POLICIES pp ON a.ID=pp.APPLICATION_ID INNER JOIN LOCKOUT_POLICIES lp ON lp.APPLICATION_ID=pp.APPLICATION_ID "
			+ "INNER JOIN LOGIN_POLICIES lgp ON lgp.APPLICATION_ID=lp.APPLICATION_ID "
			+ "WHERE u.USER_NAME=? AND a.NAME=?";

	private static final String FAILED_PASSWORD_ATTEMPT_UPDATE_QUERY = "UPDATE MEMBERSHIPS SET FAILED_PWD_ATMPT_WIN_START = ?, FAILED_PWD_ATMPT_CNT=? WHERE ID=?";

	private static final String FAILED_PASSWORD_ATTEMPT_COUNT_UPDATE_QUERY = "UPDATE MEMBERSHIPS SET FAILED_PWD_ATMPT_CNT=? WHERE ID=?";

	private static final String LOCK_UPDATE_QUERY = "UPDATE MEMBERSHIPS SET LOCKED=?, LAST_LOCK_OUT_DATE=? WHERE ID=?";

	private static final String USER_UPDATE_QUERY = "UPDATE USERS SET LAST_ACTIVITY_DATE=?, LAST_UPDATE_DATE=?, UPDATER=? WHERE ID=?";

	private static final String COUNT_APPLICATIONS_QUERY = "SELECT COUNT(ID) FROM APPLICATIONS WHERE NAME=?";

	private static final String COUNT_MEMBERSHIPS_QUERY = "SELECT COUNT(m.ID) FROM MEMBERSHIPS m INNER JOIN USERS u ON m.USER_ID=u.ID INNER JOIN applications a on u.APPLICATION_ID=a.ID WHERE u.USER_NAME=? AND a.name=?";

	private static final String LOGIN_HISTORY_SELECT_QUERY = "SELECT HISTORY_ID FROM LOGIN_HISTORIES WHERE MEMBERSHIP_ID=? ORDER BY LOGIN_DATE DESC";

	private static final String LOGIN_HISTORY_INSERT_QUERY = "INSERT INTO LOGIN_HISTORIES(MEMBERSHIP_ID,HISTORY_ID,LOGIN_DATE) values (?,?,?)";

	private static final String LOGIN_HISTORY_DELETE_QUERY = "DELETE FROM LOGIN_HISTORIES WHERE MEMBERSHIP_ID=:membership_id AND HISTORY_ID IN (:history_ids)";

	private static final int MIN = 1;
	private static final int SML = 4;
	private static final int MED = 11;
	private static final int MAX = 51;

	public PasswordAttemptUpdaterImpl() {
	}

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	private boolean applicationExists() {
		return getJdbcTemplate().queryForObject(COUNT_APPLICATIONS_QUERY, new String[] { this.getApplicationName() },
				new RowMapper<Boolean>() {
					@Override
					public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getInt(1) == 1;
					}
				});
	}

	private boolean membershipExists(String userName) {
		return getJdbcTemplate().queryForObject(COUNT_MEMBERSHIPS_QUERY,
				new String[] { userName, this.getApplicationName() }, new RowMapper<Boolean>() {
					@Override
					public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
						return rs.getInt(1) == 1;
					}
				});
	}

	@Override
	public void update(String userName, String password) {
		checkDependencies();
		if (!applicationExists()) {
			throw new ApplicationNotFoundException(this.getApplicationName());
		}
		if (!membershipExists(userName)) {
			throw new CredentialsNotFoundException(userName);
		}
		final PasswordEncoder pwdEncoder = this.getPasswordEncoder();
		getJdbcTemplate().query(SELECT_QUERY, new String[] { userName, this.getApplicationName() },
				new RowCallbackHandler() {
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						final long userId = rs.getLong("USER_ID");
						final long membershipId = rs.getLong("MEMBERSHIP_ID");
						final String encodedPassword = rs.getString("PASSWORD");
						final Timestamp failedPasswordAttemptWindowStart = rs
								.getTimestamp("FAILED_PWD_ATMPT_WIN_START");
						final short failedPasswordAttemptCount = rs.getShort("FAILED_PWD_ATMPT_CNT");
						final short attemptWindow = rs.getShort("ATTEMPT_WINDOW");
						final short lockoutThreshold = rs.getShort("LOCK_OUT_THRESHOLD");
						final short loginHistoryThreshold = rs.getShort("LOGIN_HISTORY_THRESHOLD");

						Timestamp now = new Timestamp(Calendar.getInstance().getTimeInMillis());
						Calendar c = Calendar.getInstance();

						if (!pwdEncoder.matches(password, encodedPassword)) {
							c.setTime(failedPasswordAttemptWindowStart);
							c.add(Calendar.MINUTE, attemptWindow);
							if (now.getTime() > c.getTimeInMillis()) {
								getJdbcTemplate().update(new PreparedStatementCreator() {
									@Override
									public PreparedStatement createPreparedStatement(Connection con)
											throws SQLException {
										PreparedStatement preparedStatement = con
												.prepareStatement(FAILED_PASSWORD_ATTEMPT_UPDATE_QUERY);
										preparedStatement.setTimestamp(1, now);
										preparedStatement.setInt(2, 1);
										preparedStatement.setLong(3, membershipId);
										return preparedStatement;
									}
								});
							} else {

								getJdbcTemplate().update(new PreparedStatementCreator() {
									@Override
									public PreparedStatement createPreparedStatement(Connection con)
											throws SQLException {
										PreparedStatement preparedStatement = con
												.prepareStatement(FAILED_PASSWORD_ATTEMPT_COUNT_UPDATE_QUERY);
										preparedStatement.setInt(1, failedPasswordAttemptCount + 1);
										preparedStatement.setLong(2, membershipId);
										return preparedStatement;
									}
								});
							}
							// We want locking an account to be possible if and
							// only if
							// the lockout threshold is greater than 0.
							if (failedPasswordAttemptCount >= lockoutThreshold && lockoutThreshold > 0) {
								getJdbcTemplate().update(new PreparedStatementCreator() {
									@Override
									public PreparedStatement createPreparedStatement(Connection con)
											throws SQLException {
										PreparedStatement preparedStatement = con.prepareStatement(LOCK_UPDATE_QUERY);
										preparedStatement.setBoolean(1, true);
										preparedStatement.setTimestamp(2, now);
										preparedStatement.setLong(3, membershipId);
										return preparedStatement;
									}
								});
							}
						} else {
							if (failedPasswordAttemptCount > 0) {
								c.setTime(DateUtil.toDate("1754-01-01"));
								getJdbcTemplate().update(new PreparedStatementCreator() {
									@Override
									public PreparedStatement createPreparedStatement(Connection con)
											throws SQLException {
										PreparedStatement preparedStatement = con
												.prepareStatement(FAILED_PASSWORD_ATTEMPT_UPDATE_QUERY);
										preparedStatement.setDate(1, new java.sql.Date(c.getTimeInMillis()));
										preparedStatement.setInt(2, 0);
										preparedStatement.setLong(3, userId);
										return preparedStatement;
									}
								});
							}

							final List<Integer> undeletable_history_ids = new ArrayList<>();
							final List<Integer> deletable_history_ids = new ArrayList<>();

							getJdbcTemplate().query(LOGIN_HISTORY_SELECT_QUERY, new Long[] { membershipId },
									new RowCallbackHandler() {
										@Override
										public void processRow(ResultSet rs) throws SQLException {
											final int historyId = rs.getInt("HISTORY_ID");
											if (rs.getRow() >= loginHistoryThreshold) {
												deletable_history_ids.add(historyId);
											} else {
												undeletable_history_ids.add(historyId);
											}
										}
									});

							if (!deletable_history_ids.isEmpty()) {
								deleteLoginHistories(membershipId, deletable_history_ids);
							}

							getJdbcTemplate().update(new PreparedStatementCreator() {
								@Override
								public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
									PreparedStatement preparedStatement = con
											.prepareStatement(LOGIN_HISTORY_INSERT_QUERY);
									preparedStatement.setLong(1, membershipId);
									preparedStatement.setInt(2, getLeastAvailable(undeletable_history_ids));
									preparedStatement.setTimestamp(3, now);
									return preparedStatement;
								}
							});
						}

						getJdbcTemplate().update(new PreparedStatementCreator() {
							@Override
							public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
								PreparedStatement preparedStatement = con.prepareStatement(USER_UPDATE_QUERY);
								preparedStatement.setTimestamp(1, now);
								preparedStatement.setTimestamp(2, now);
								preparedStatement.setString(3, userName);
								preparedStatement.setLong(4, userId);
								return preparedStatement;
							}
						});
					}
				});
	}

	private void checkDependencies() {
		if (this.getApplicationName() == null) {
			throw new IllegalStateException("The application name cannot be null.");
		}
		if (this.getPasswordEncoder() == null) {
			throw new IllegalStateException("The password encoder cannot be null.");
		}
	}

	private void deleteLoginHistories(Long membershipId, List<Integer> historyIds) {
		getJdbcTemplate().execute((Connection con) -> {

			LinkedList<Integer> ids = new LinkedList<>(historyIds);
			int remainder = ids.size();
			int updated = 0;

			while (remainder > 0) {
				// identify the batch size for this execution.
				int batchSize;
				if (remainder >= MAX) {
					batchSize = MAX;
				} else if (remainder >= MED) {
					batchSize = MED;
				} else if (remainder >= SML) {
					batchSize = SML;
				} else {
					batchSize = MIN;
				}
				remainder -= batchSize;

				// Build the in-clause parameters.
				StringBuilder inClause = new StringBuilder(batchSize * 2);
				for (int i = 0; i < batchSize; i++) {
					if (i > 0) {
						inClause.append(',');
					}
					inClause.append('?');
				}

				PreparedStatement ps = con.prepareStatement(LOGIN_HISTORY_DELETE_QUERY
						.replace(":membership_Id", String.valueOf(membershipId)).replace(":history_ids", inClause.toString()));
				for (int i = 0; i < batchSize; i++) {
					ps.setInt(i + 1, ids.pop());
				}
				updated += ps.executeUpdate();
			}
			return updated;
		});
	}

	private int getLeastAvailable(List<Integer> numbers) {
		if (numbers.isEmpty()) {
			return 1;
		}
		Collections.sort(numbers);
		final Integer max = numbers.get(numbers.size() - 1);

		if (max == null || max == 0) {
			return 1;
		}
		for (int i = 1; i < max; i++) {
			if (!numbers.contains(i))
				return i;
		}
		return max + 1;
	}
}