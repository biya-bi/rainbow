package org.rainbow.security.service.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class WrongRecoveryAnswerException extends RainbowSecurityServiceException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -16909515899844654L;
	private final String userName;
    private final String question;

    public WrongRecoveryAnswerException(String userName, String question) {
        this(userName, question, String.format("The user '%s' provided a wrong answer to the recovery question '%s'.", userName, question));
    }

    public WrongRecoveryAnswerException(String userName, String question, String message) {
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
