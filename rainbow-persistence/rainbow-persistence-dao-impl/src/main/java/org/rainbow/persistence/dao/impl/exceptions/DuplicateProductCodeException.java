/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.persistence.dao.impl.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class DuplicateProductCodeException extends RainbowShoppingCartException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2107492470349476427L;
	private final String code;

    public DuplicateProductCodeException(String number) {
        this(number, String.format("A product with code '%s' already exists.", number));
    }

    public DuplicateProductCodeException(String code,  String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
