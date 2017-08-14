package org.rainbow.security.core.jdbc;

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
public class SensitiveUserDetailsEncrypter extends JdbcDaoSupport {
	private final String selectQuery = "SELECT u.USER_NAME, m.PASSWORD, m.ENABLED, PASSWORD_QUESTION_ANSWER FROM USERS u INNER "
			+ "JOIN MEMBERSHIPS m ON u.ID=m.USER_ID INNER JOIN APPLICATIONS a ON u.APPLICATION_ID=a.ID WHERE "
			+ "(m.ENCRYPTED=0 OR m.ENCRYPTED IS NULL) AND a.NAME=?";

	private final String updateQuery = "UPDATE MEMBERSHIPS SET PASSWORD = ?,PASSWORD_QUESTION_ANSWER=?, ENCRYPTED=1 WHERE "
			+ "USER_ID=(SELECT u.ID FROM USERS u INNER JOIN APPLICATIONS a WHERE "
			+ "u.APPLICATION_ID=a.ID AND u.USER_NAME =? AND a.NAME=?)";

	private final PasswordEncoder passwordEncoder;
	private final String applicationName;

	public SensitiveUserDetailsEncrypter(PasswordEncoder passwordEncoder, String applicationName) {
		super();
		if (passwordEncoder == null)
			throw new IllegalArgumentException("The passwordEncoder argument cannot be null.");
		if (applicationName == null)
			throw new IllegalArgumentException("The applicationName argument cannot be null.");
		this.passwordEncoder = passwordEncoder;
		this.applicationName = applicationName;
	}

	/**
	 * Encrypt db sensitive details.
	 */
	public void encrypt() {
		getJdbcTemplate().query(getSelectQuery(), new String[] { applicationName }, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				final String username = rs.getString("USER_NAME");
				final String encryptedPassword = passwordEncoder.encode(rs.getString("PASSWORD"));
				final String securityQuestioAnswer = rs.getString("PASSWORD_QUESTION_ANSWER");
				final String encryptedSecurityQuestionAnswer = passwordEncoder
						.encode(securityQuestioAnswer != null ? securityQuestioAnswer.toUpperCase() : "");
				getJdbcTemplate().update(new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
						PreparedStatement preparedStatement = con.prepareStatement(getUpdateQuery());
						preparedStatement.setString(1, encryptedPassword);
						preparedStatement.setString(2, encryptedSecurityQuestionAnswer);
						preparedStatement.setString(3, username);
						preparedStatement.setString(4, applicationName);
						return preparedStatement;
					}
				});
			}
		});
	}

	/**
	 * Gets the password encoder.
	 * 
	 * @return the password encoder
	 */
	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	/**
	 * Gets the select query.
	 * 
	 * @return the select query
	 */
	public String getSelectQuery() {
		return selectQuery;
	}

	/**
	 * Gets the update query.
	 * 
	 * @return the update query
	 */
	public String getUpdateQuery() {
		return updateQuery;
	}

}