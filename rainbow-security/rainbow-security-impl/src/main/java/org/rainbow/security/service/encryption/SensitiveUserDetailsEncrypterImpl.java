package org.rainbow.security.service.encryption;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 
 * @author Biya-Bi
 */
public class SensitiveUserDetailsEncrypterImpl extends JdbcDaoSupport implements SensitiveUserDetailsEncrypter {
	private static final String PASSWORDS_SELECT_QUERY = "SELECT m.ID AS MEMBERSHIP_ID, m.PASSWORD, m.ENABLED FROM USERS u INNER "
			+ "JOIN MEMBERSHIPS m ON u.ID=m.USER_ID INNER JOIN APPLICATIONS a ON u.APPLICATION_ID=a.ID WHERE "
			+ "(m.ENCRYPTED=0 OR m.ENCRYPTED IS NULL) AND a.NAME=?";

	private static final String PASSWORDS_UPDATE_QUERY = "UPDATE MEMBERSHIPS SET PASSWORD=?, ENCRYPTED=1 WHERE ID=?";

	private static final String RECOVERY_ANSWERS_SELECT_QUERY = "SELECT ri.ID, ri.ANSWER FROM RECOVERY_INFORMATION ri INNER JOIN MEMBERSHIPS m "
			+ "ON ri.MEMBERSHIP_ID=m.ID INNER JOIN USERS u ON m.USER_ID=u.ID INNER JOIN APPLICATIONS a ON u.APPLICATION_ID=a.ID WHERE "
			+ "(ri.ENCRYPTED=0 OR ri.ENCRYPTED IS NULL) AND a.NAME=?";

	private static final String RECOVERY_ANSWERS_UPDATE_QUERY = "UPDATE RECOVERY_INFORMATION SET ANSWER=?, ENCRYPTED=1 WHERE ID=?";

	private PasswordEncoder passwordEncoder;
	private String applicationName;

	public SensitiveUserDetailsEncrypterImpl() {
		super();
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

	@Override
	public void encrypt() {
		checkDependencies();
		final PasswordEncoder pwdEncoder = this.getPasswordEncoder();
		getJdbcTemplate().query(PASSWORDS_SELECT_QUERY, new String[] { this.getApplicationName() },
				new RowCallbackHandler() {
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						final Long membershipId = rs.getLong("MEMBERSHIP_ID");
						final String encryptedPassword = pwdEncoder.encode(rs.getString("PASSWORD"));
						getJdbcTemplate().update(new PreparedStatementCreator() {
							@Override
							public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
								PreparedStatement preparedStatement = con.prepareStatement(PASSWORDS_UPDATE_QUERY);
								preparedStatement.setString(1, encryptedPassword);
								preparedStatement.setLong(2, membershipId);
								return preparedStatement;
							}
						});
					}
				});

		getJdbcTemplate().query(RECOVERY_ANSWERS_SELECT_QUERY, new String[] { this.getApplicationName() },
				new RowCallbackHandler() {
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						final Long id = rs.getLong("ID");
						final String encryptedAnswer = pwdEncoder.encode(rs.getString("ANSWER"));
						getJdbcTemplate().update(new PreparedStatementCreator() {
							@Override
							public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
								PreparedStatement preparedStatement = con
										.prepareStatement(RECOVERY_ANSWERS_UPDATE_QUERY);
								preparedStatement.setString(1, encryptedAnswer);
								preparedStatement.setLong(2, id);
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

}