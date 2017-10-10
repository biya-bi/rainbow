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
	private final String passwordsSelectQuery = "SELECT m.USER_ID, m.PASSWORD, m.ENABLED FROM USERS u INNER "
			+ "JOIN MEMBERSHIPS m ON u.ID=m.USER_ID INNER JOIN APPLICATIONS a ON u.APPLICATION_ID=a.ID WHERE "
			+ "(m.ENCRYPTED=0 OR m.ENCRYPTED IS NULL) AND a.NAME=?";

	private final String passwordsUpdateQuery = "UPDATE MEMBERSHIPS SET PASSWORD=?, ENCRYPTED=1 WHERE USER_ID=?";

	private final String recoveryAnswersSelectQuery = "SELECT ard.ID, ard.ANSWER FROM RECOVERY_INFORMATION ard INNER JOIN MEMBERSHIPS m "
			+ "ON ard.USER_ID=m.USER_ID INNER JOIN USERS u ON m.USER_ID=u.ID INNER JOIN APPLICATIONS a ON u.APPLICATION_ID=a.ID WHERE "
			+ "(ard.ENCRYPTED=0 OR ard.ENCRYPTED IS NULL) AND a.NAME=?";

	private final String recoveryAnswersUpdateQuery = "UPDATE RECOVERY_INFORMATION SET ANSWER=?, ENCRYPTED=1 WHERE ID=?";

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
		getJdbcTemplate().query(passwordsSelectQuery, new String[] { this.getApplicationName() },
				new RowCallbackHandler() {
					@Override
					public void processRow(ResultSet rs) throws SQLException {
						final Long userId = rs.getLong("USER_ID");
						final String encryptedPassword = pwdEncoder.encode(rs.getString("PASSWORD"));
						getJdbcTemplate().update(new PreparedStatementCreator() {
							@Override
							public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
								PreparedStatement preparedStatement = con.prepareStatement(passwordsUpdateQuery);
								preparedStatement.setString(1, encryptedPassword);
								preparedStatement.setLong(2, userId);
								return preparedStatement;
							}
						});
					}
				});

		getJdbcTemplate().query(recoveryAnswersSelectQuery, new String[] { this.getApplicationName() }, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				final Long id = rs.getLong("ID");
				final String encryptedAnswer = pwdEncoder.encode(rs.getString("ANSWER"));
				getJdbcTemplate().update(new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						PreparedStatement preparedStatement = con.prepareStatement(recoveryAnswersUpdateQuery);
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