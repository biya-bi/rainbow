/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.shopping.cart.persistence.exceptions;

import javax.ejb.ApplicationException;

/**
 *
 * @author Biya-Bi
 */
@ApplicationException(rollback = true)
public class RainbowShoppingCartException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2302908330419228601L;

	public RainbowShoppingCartException() {
    }

    public RainbowShoppingCartException(String message) {
        super(message);
    }

    public RainbowShoppingCartException(String message, Throwable cause) {
        super(message, cause);
    }

    public RainbowShoppingCartException(Throwable cause) {
        super(cause);
    }

    public RainbowShoppingCartException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
