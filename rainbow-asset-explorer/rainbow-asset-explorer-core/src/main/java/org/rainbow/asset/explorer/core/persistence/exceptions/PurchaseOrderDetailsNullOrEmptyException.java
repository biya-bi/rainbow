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
public class PurchaseOrderDetailsNullOrEmptyException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8246264238823192698L;

	public PurchaseOrderDetailsNullOrEmptyException() {
        super("A purchase order's details cannot be null and must contain at least one product with a positive quantity.");
    }

    public PurchaseOrderDetailsNullOrEmptyException(String message) {
        super(message);
    }
}
