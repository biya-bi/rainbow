package org.rainbow.security.service.exceptions;

public class RecoveryQuestionNotFoundException extends RainbowSecurityServiceException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7484941572155678040L;
	private final String userName;
	private final String question;

	public RecoveryQuestionNotFoundException(String userName, String question) {
		this(userName, question,
				String.format("The user '%s' has no account recovery information with question '%s'.", userName, question));
	}

	public RecoveryQuestionNotFoundException(String userName, String question, String message) {
		super(message);
		this.userName = userName;
		this.question = question;
	}

	public String getUserName() {
		return userName;
	}

	public String getQuestion() {
		return question;
	}

}
