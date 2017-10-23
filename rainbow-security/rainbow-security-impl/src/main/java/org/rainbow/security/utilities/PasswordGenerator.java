package org.rainbow.security.utilities;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Biya-Bi This class can generate random passwords, which do not
 *         include ambiguous characters, such as I, l, and 1. The generated
 *         password will be made of 7-bit ASCII symbols. Every four characters
 *         will include one lower case character, one upper case character, one
 *         number, and one special symbol (such as '%') in a random order. The
 *         password will always start with an alpha-numeric character; it will
 *         not start with a special symbol (we do this because some back-end
 *         systems do not like certain special characters in the first
 *         position).
 */
public class PasswordGenerator {
	// Define default min and max password lengths.

	private static final int DEFAULT_MIN_PASSWORD_LENGTH = 8;
	private static final int DEFAULT_MAX_PASSWORD_LENGTH = 10;

	// Define supported password characters divided into groups.
	// You can add (or remove) characters to (from) these groups.
	private static final String PASSWORD_CHARS_LCASE = "abcdefgijkmnopqrstwxyz";
	private static final String PASSWORD_CHARS_UCASE = "ABCDEFGHJKLMNOPQRSTWXYZ";
	private static final String PASSWORD_CHARS_NUMERIC = "0123456789";
	private static final String PASSWORD_CHARS_SPECIAL = "!@#$%^&*()_+=-{}[]|:;\\/?<>,.`~'";

	private int minLength;
	private int maxLength;
	private int minSpecialChars;
	private int minLowerCase;
	private int minUpperCase;
	private int minNumeric;

	public PasswordGenerator() {
		this(DEFAULT_MIN_PASSWORD_LENGTH, DEFAULT_MAX_PASSWORD_LENGTH, 0, 0, 0, 0);
	}

	public PasswordGenerator(int length) {
		this(length, length, 0, 0, 0, 0);
	}

	public PasswordGenerator(int minLength, int maxLength) {
		this(minLength, maxLength, 0, 0, 0, 0);
	}

	public PasswordGenerator(int minLength, int maxLength, int minSpecialChars) {
		this(minLength, maxLength, minSpecialChars, 0, 0, 0);
	}

	public PasswordGenerator(int minLength, int maxLength, int minSpecialChars, int minLowerCase, int minUpperCase,
			int minNumeric) {
		this.minLength = minLength;
		this.maxLength = maxLength;
		this.minSpecialChars = minSpecialChars;
		this.minLowerCase = minLowerCase;
		this.minUpperCase = minUpperCase;
		this.minNumeric = minNumeric;
	}

	public String generate() {
		// Create a local array containing supported password characters
		// grouped by types. You can remove character groups from this
		// array, but doing so will weaken the password strength.
		char[][] charGroups = new char[][] { PASSWORD_CHARS_LCASE.toCharArray(), PASSWORD_CHARS_UCASE.toCharArray(),
				PASSWORD_CHARS_NUMERIC.toCharArray(), PASSWORD_CHARS_SPECIAL.toCharArray() };

		// Use this array to track the number of unused characters in each
		// character group.
		int[] charsLeftInGroup = new int[charGroups.length];

		// Initially, all characters in each group are not used.
		for (int i = 0; i < charsLeftInGroup.length; i++) {
			charsLeftInGroup[i] = charGroups[i].length;
		}

		// Use this array to track (iterate through) unused character groups.
		int[] leftGroupsOrder = new int[charGroups.length];

		// Initially, all character groups are not used.
		for (int i = 0; i < leftGroupsOrder.length; i++) {
			leftGroupsOrder[i] = i;
		}

		SecureRandom random = new SecureRandom();
		byte randomBytes[] = new byte[4];
		random.nextBytes(randomBytes);

		// This array will hold password characters.
		char[] password = null;

		// Allocate appropriate memory for the password.
		if (minLength < maxLength) {
			password = new char[minLength + random.nextInt(maxLength - minLength + 1)];
		} else {
			password = new char[minLength];
		}
		List<String> characterSourceList = new ArrayList<>();
		characterSourceList.add(PASSWORD_CHARS_LCASE);
		characterSourceList.add(PASSWORD_CHARS_UCASE);
		characterSourceList.add(PASSWORD_CHARS_NUMERIC);
		characterSourceList.add(PASSWORD_CHARS_SPECIAL);

		Map<String, Integer> characterSourceMap = new HashMap<>();
		characterSourceMap.put(PASSWORD_CHARS_LCASE, minLowerCase);
		characterSourceMap.put(PASSWORD_CHARS_UCASE, minUpperCase);
		characterSourceMap.put(PASSWORD_CHARS_NUMERIC, minNumeric);
		characterSourceMap.put(PASSWORD_CHARS_SPECIAL, minSpecialChars);
		while (!characterSourceList.isEmpty()) {
			String characterSource = characterSourceList.get(random.nextInt(characterSourceList.size()));
			Integer count = characterSourceMap.get(characterSource);
			characterSourceList.remove(characterSource);
			setMinimumChars(characterSource, password, count, random);
		}
		// Index of the next character to be added to password.
		int nextCharIdx;

		// Index of the next character group to be processed.
		int nextGroupIdx;

		// Index which will be used to track not processed character groups.
		int nextLeftGroupsOrderIdx;

		// Index of the last non-processed character in a group.
		int lastCharIdx;

		// Index of the last non-processed group.
		int lastLeftGroupsOrderIdx = leftGroupsOrder.length - 1;

		// generate password characters one at a time.
		for (int i = 0; i < password.length; i++) {
			if (password[i] != '\0') {
				continue;
			}
			// If only one character group remained unprocessed, process it;
			// otherwise, pick a random character group from the unprocessed
			// group list. To allow a special character to appear in the
			// first position, increment the second parameter of the Next
			// function call by one, i.e. lastLeftGroupsOrderIdx + 1.
			if (lastLeftGroupsOrderIdx == 0) {
				nextLeftGroupsOrderIdx = 0;
			} else {
				nextLeftGroupsOrderIdx = random.nextInt(lastLeftGroupsOrderIdx + 1); // Biya-Bi
																						// incremented
																						// this
																						// by
																						// one.
			}
			// Get the actual index of the character group, from which we will
			// pick the next character.
			nextGroupIdx = leftGroupsOrder[nextLeftGroupsOrderIdx];

			// Get the index of the last unprocessed characters in this group.
			lastCharIdx = charsLeftInGroup[nextGroupIdx] - 1;

			// If only one unprocessed character is left, pick it; otherwise,
			// get a random character from the unused character list.
			if (lastCharIdx == 0) {
				nextCharIdx = 0;
			} else {
				nextCharIdx = random.nextInt(lastCharIdx + 1);
			}

			// Add this character to the password.
			password[i] = charGroups[nextGroupIdx][nextCharIdx];

			// If we processed the last character in this group, start over.
			if (lastCharIdx == 0) {
				charsLeftInGroup[nextGroupIdx] = charGroups[nextGroupIdx].length;
			}
			// There are more unprocessed characters left.
			else {
				// Swap processed character with the last unprocessed character
				// so that we don't pick it until we process all characters in
				// this group.
				if (lastCharIdx != nextCharIdx) {
					char temp = charGroups[nextGroupIdx][lastCharIdx];
					charGroups[nextGroupIdx][lastCharIdx] = charGroups[nextGroupIdx][nextCharIdx];
					charGroups[nextGroupIdx][nextCharIdx] = temp;
				}
				// Decrement the number of unprocessed characters in
				// this group.
				charsLeftInGroup[nextGroupIdx]--;
			}

			// If we processed the last group, start all over.
			if (lastLeftGroupsOrderIdx == 0) {
				lastLeftGroupsOrderIdx = leftGroupsOrder.length - 1;
			}
			// There are more unprocessed groups left.
			else {
				// Swap processed group with the last unprocessed group
				// so that we don't pick it until we process all groups.
				if (lastLeftGroupsOrderIdx != nextLeftGroupsOrderIdx) {
					int temp = leftGroupsOrder[lastLeftGroupsOrderIdx];
					leftGroupsOrder[lastLeftGroupsOrderIdx] = leftGroupsOrder[nextLeftGroupsOrderIdx];
					leftGroupsOrder[nextLeftGroupsOrderIdx] = temp;
				}
				// Decrement the number of unprocessed groups.
				lastLeftGroupsOrderIdx--;
			}
		}

		// Convert password characters into a string and return the result.
		return new String(password);
	}

	private char[] setMinimumChars(String characterSource, char[] password, int minNonalphanumericChars,
			Random random) {
		char[] characterSourceArray = characterSource.toCharArray();
		int lastCharIdx = characterSourceArray.length - 1;
		int nextCharIdx;

		List<Integer> emptyIndicesList = new ArrayList<>();
		for (int i = 0; i < password.length; i++) {
			// If there is no charecter at that index,add the index to the empty
			// indices list.
			if (password[i] == '\0') {
				emptyIndicesList.add(i);
			}
		}

		// If there are no empty indices available, return the password array.
		if (emptyIndicesList.isEmpty()) {
			return password;
		}

		Integer[] leftPwdIndicesOrder = new Integer[emptyIndicesList.size()];
		emptyIndicesList.toArray(leftPwdIndicesOrder);

		int lastPwdIdx = leftPwdIndicesOrder.length - 1;
		int nextPwdIdx;

		for (int i = 0; i < minNonalphanumericChars; i++) {
			// If only one unprocessed character is left, pick it; otherwise,
			// get a random character from the unused character list.
			if (lastCharIdx == 0) {
				nextCharIdx = 0;
			} else {
				nextCharIdx = random.nextInt(lastCharIdx + 1);
			}

			if (lastPwdIdx == 0) {
				nextPwdIdx = 0;
			} else {
				nextPwdIdx = random.nextInt(lastPwdIdx + 1);
			}

			int j = leftPwdIndicesOrder[nextPwdIdx];

			if (lastPwdIdx != nextPwdIdx) {
				int tmp = leftPwdIndicesOrder[nextPwdIdx];
				leftPwdIndicesOrder[nextPwdIdx] = leftPwdIndicesOrder[lastPwdIdx];
				leftPwdIndicesOrder[lastPwdIdx] = tmp;
			}

			lastPwdIdx--;

			// Add this character to the password.
			password[j] = characterSourceArray[nextCharIdx];

			// If we processed the last character in this group, start over.
			if (lastCharIdx == 0) {
				lastCharIdx = characterSourceArray.length - 1;
			}
			// There are more unprocessed characters left.
			else {
				// Swap processed character with the last unprocessed character
				// so that we don't pick it until we process all characters in
				// this group.
				if (lastCharIdx != nextCharIdx) {
					char temp = characterSourceArray[lastCharIdx];
					characterSourceArray[lastCharIdx] = characterSourceArray[nextCharIdx];
					characterSourceArray[nextCharIdx] = temp;
				}
				// Decrement the number of unprocessed characters in
				// this group.
				lastCharIdx--;
			}

		}
		return password;
	}

	public int getMinLength() {
		return minLength;
	}

	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public int getMinSpecialChars() {
		return minSpecialChars;
	}

	public void setMinSpecialChars(int minSpecialChars) {
		this.minSpecialChars = minSpecialChars;
	}

	public int getMinLowerCase() {
		return minLowerCase;
	}

	public void setMinLowerCase(int minLowerCase) {
		this.minLowerCase = minLowerCase;
	}

	public int getMinUpperCase() {
		return minUpperCase;
	}

	public void setMinUpperCase(int minUpperCase) {
		this.minUpperCase = minUpperCase;
	}

	public int getMinNumeric() {
		return minNumeric;
	}

	public void setMinNumeric(int minNumeric) {
		this.minNumeric = minNumeric;
	}

}
