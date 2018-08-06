package org.rainbow.security.service.authentication;

/**
 * Allows implementations to update user details based on the correctness of
 * credentials provided during authentication. For example, a user may be locked
 * by the {@code PasswordAttemptUpdater.update} method if a wrong password was
 * provided a certain number of times within a given time frame.
 * 
 * @author Biya-Bi
 *
 */
public interface PasswordAttemptUpdater {
	/**
	 * Updates user details based on the correctness of the provided credentials
	 * 
	 * @param userName
	 *            the user name identifying the user
	 * @param password
	 *            the password provided by the user
	 */
	void update(String userName, String password);

}
