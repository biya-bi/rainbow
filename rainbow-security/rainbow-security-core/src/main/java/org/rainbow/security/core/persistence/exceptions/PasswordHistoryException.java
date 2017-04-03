/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.security.core.persistence.exceptions;

import javax.xml.ws.WebFault;

/**
 *
 * @author Biya-Bi
 */
@WebFault(name = "PasswordHistoryFault")
public class PasswordHistoryException extends RainbowSecurityException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -714180864430046507L;
	private int passwordHistoryLength;

    public PasswordHistoryException(int passwordHistoryLength) {
        this(passwordHistoryLength, String.format("The Enforce Password History policy requires that any new password should not occur in the last '%s' used passwords.", passwordHistoryLength));
    }

    public PasswordHistoryException(int passwordHistoryLength, String message) {
        super(message);
        this.passwordHistoryLength = passwordHistoryLength;
    }

    public int getPasswordHistoryLength() {
        return passwordHistoryLength;
    }

}
