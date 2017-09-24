package org.rainbow.security.service.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class DuplicateRecoveryQuestionException extends RainbowSecurityServiceException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7460718216606829815L;
	private final String userName;
	private final String question;

	public DuplicateRecoveryQuestionException(String userName, String question) {
		this(userName, question,
				String.format("The user '%s' already has the recovery question '%s'.", userName, question));
	}

	public DuplicateRecoveryQuestionException(String userName, String question, String message) {
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
