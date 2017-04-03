/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.security.core.persistence.exceptions;

import javax.ejb.ApplicationException;
import javax.xml.ws.WebFault;

/**
 *
 * @author Biya-Bi
 */
@WebFault(name = "InvalidPasswordFault")
@ApplicationException(rollback = true)
public class InvalidPasswordException extends RainbowSecurityException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1604836408076504303L;

	public InvalidPasswordException() {
        super("An invalid password was specified. Contact your support team for more details on the password policy.");
    }

    public InvalidPasswordException(String message) {
        super(message);
    }

}
