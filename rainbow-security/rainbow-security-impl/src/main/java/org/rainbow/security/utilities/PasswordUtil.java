package org.rainbow.security.utilities;

import org.rainbow.security.orm.entities.PasswordPolicy;

public class PasswordUtil {
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

}
