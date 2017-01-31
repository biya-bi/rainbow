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
public class DuplicateProductNameException extends RainbowShoppingCartException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8562525775096945294L;
	private final String name;

    public DuplicateProductNameException(String name) {
        this(name, String.format("A product with name '%s' already exists.", name));
    }

    public DuplicateProductNameException(String name,  String message) {
        super(message);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
