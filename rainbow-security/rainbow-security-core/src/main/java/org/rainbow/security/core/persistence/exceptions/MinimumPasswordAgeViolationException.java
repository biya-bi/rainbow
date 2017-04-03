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
@WebFault(name = "PasswordChangeFault")
public class MinimumPasswordAgeViolationException extends RainbowSecurityException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 670286918425635879L;

	public MinimumPasswordAgeViolationException() {
        super("A password cannot be change if it has not been used for at least the number of days specified in the Minimum Password Age policy.");
    }

    public MinimumPasswordAgeViolationException(String message) {
        super(message);
    }
    
}
