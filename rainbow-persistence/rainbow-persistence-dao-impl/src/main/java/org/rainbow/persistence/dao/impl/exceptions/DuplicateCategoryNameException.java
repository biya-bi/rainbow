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
public class DuplicateCategoryNameException extends RainbowShoppingCartException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8716677053110179407L;
	private final String name;

	public DuplicateCategoryNameException(String name) {
		this(name, String.format("A category with name '%s' already exists.", name));
	}

	public DuplicateCategoryNameException(String name, String message) {
		super(message);
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
