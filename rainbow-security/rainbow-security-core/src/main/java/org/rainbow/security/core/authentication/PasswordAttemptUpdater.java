package org.rainbow.security.core.authentication;

public interface PasswordAttemptUpdater {

	void update(String userName, String password);

}
