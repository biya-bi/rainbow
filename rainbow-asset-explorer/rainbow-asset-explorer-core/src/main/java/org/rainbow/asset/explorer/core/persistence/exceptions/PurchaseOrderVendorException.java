/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.persistence.exceptions;

/**
 * This exception is thrown whenever an inactive vendor is specified
 * for a purchase order that is pending.
 *
 * @author Biya-Bi
 */
public class PurchaseOrderVendorException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7608103102650164360L;

	public PurchaseOrderVendorException() {
        this("An inactive vendor cannot be specified for a purchase order that is pending.");
    }

    public PurchaseOrderVendorException(String message) {
        super(message);
    }
}
