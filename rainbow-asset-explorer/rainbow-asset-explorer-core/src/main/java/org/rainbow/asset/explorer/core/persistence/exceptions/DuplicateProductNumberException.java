/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.persistence.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class DuplicateProductNumberException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2107492470349476427L;
	private final String number;

    public DuplicateProductNumberException(String number) {
        this(number, String.format("A product with number '%s' already exists.", number));
    }

    public DuplicateProductNumberException(String number,  String message) {
        super(message);
        this.number = number;
    }

    public String getNumber() {
        return number;
    }
}
