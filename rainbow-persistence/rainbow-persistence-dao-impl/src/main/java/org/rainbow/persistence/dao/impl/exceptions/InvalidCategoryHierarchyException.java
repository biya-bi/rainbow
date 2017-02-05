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
public class InvalidCategoryHierarchyException extends RainbowShoppingCartException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7969646204361785176L;

	public InvalidCategoryHierarchyException() {
		this("A category cannot be a parent of itself, or cannot be a child of one of its descendants.");
	}

	public InvalidCategoryHierarchyException(String message) {
		super(message);
	}
}
