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
public class ShippingOrderDetailsNullOrEmptyException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8095572596777970584L;

	public ShippingOrderDetailsNullOrEmptyException() {
        super("A shipping order's details cannot be null and must contain at least one product with a positive quantity.");
    }

    public ShippingOrderDetailsNullOrEmptyException(String message) {
        super(message);
    }
}
