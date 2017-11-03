package org.rainbow.security.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import org.rainbow.security.orm.entities.PasswordPolicy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class PasswordUtil {
	private static final String SELECT_QUERY = "SELECT pp.MIN_LENGTH, pp.MAX_LENGTH,pp.MIN_UPPERCASE_CHARS_COUNT, "
			+ "pp.MIN_LOWERCASE_CHARS_COUNT, pp.MIN_NUMERIC_COUNT, pp.MIN_SPECIAL_CHARS_COUNT FROM PASSWORD_POLICIES pp "
			+ "INNER JOIN APPLICATIONS a ON pp.APPLICATION_ID=a.ID WHERE a.NAME=?";

	public static boolean isValidPassword(String password, PasswordPolicy passwordPolicy) {
		if (password == null || password.isEmpty()) {
			return false;
		}

		if (password.length() < passwordPolicy.getMinLength()) {
			return false;
		}
		if (password.length() > passwordPolicy.getMaxLength()) {
			return false;
		}
		int upperCaseCount = 0;
		int lowerCaseCount = 0;
		int digitsCount = 0;
		int specialCharCount = 0;
		for (int i = 0; i < password.length(); i++) {
			final int codePoint = password.codePointAt(i);
			if (Character.isUpperCase(codePoint)) {
				upperCaseCount++;
			} else if (Character.isLowerCase(codePoint)) {
				lowerCaseCount++;
			} else if (Character.isDigit(codePoint)) {
				digitsCount++;
			} else {
				specialCharCount++;
			}
		}
		if (upperCaseCount < passwordPolicy.getMinUppercaseCharsCount()) {
			return false;
		}
		if (lowerCaseCount < passwordPolicy.getMinLowercaseCharsCount()) {
			return false;
		}
		if (digitsCount < passwordPolicy.getMinNumericCount()) {
			return false;
		}
		if (specialCharCount < passwordPolicy.getMinSpecialCharsCount()) {
			return false;
		}
		return true;
	}

	public static boolean isValidPassword(String password, String applicationName, JdbcTemplate jdbcTemplate) {
		Objects.requireNonNull(applicationName, "The applicationName argument cannot be null.");
		Objects.requireNonNull(jdbcTemplate, "The jdbcTemplate argument cannot be null.");

		if (password == null || password.isEmpty()) {
			return false;
		}

		return jdbcTemplate.queryForObject(SELECT_QUERY, new String[] { applicationName }, new RowMapper<Boolean>() {

			@Override
			public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
				final short minLength = rs.getShort("MIN_LENGTH");
				final short maxLength = rs.getShort("MAX_LENGTH");
				final short minUppercaseCharsCount = rs.getShort("MIN_UPPERCASE_CHARS_COUNT");
				final short minLowercaseCharsCount = rs.getShort("MIN_LOWERCASE_CHARS_COUNT");
				final short minNumericCount = rs.getShort("MIN_NUMERIC_COUNT");
				final short minSpecialCharsCount = rs.getShort("MIN_SPECIAL_CHARS_COUNT");

				if (password.length() < minLength) {
					return false;
				}
				if (password.length() > maxLength) {
					return false;
				}
				int upperCaseCount = 0;
				int lowerCaseCount = 0;
				int digitsCount = 0;
				int specialCharCount = 0;
				for (int i = 0; i < password.length(); i++) {
					final int codePoint = password.codePointAt(i);
					if (Character.isUpperCase(codePoint)) {
						upperCaseCount++;
					} else if (Character.isLowerCase(codePoint)) {
						lowerCaseCount++;
					} else if (Character.isDigit(codePoint)) {
						digitsCount++;
					} else {
						specialCharCount++;
					}
				}
				if (upperCaseCount < minUppercaseCharsCount) {
					return false;
				}
				if (lowerCaseCount < minLowercaseCharsCount) {
					return false;
				}
				if (digitsCount < minNumericCount) {
					return false;
				}
				if (specialCharCount < minSpecialCharsCount) {
					return false;
				}
				return true;
			}

		});

	}

}
