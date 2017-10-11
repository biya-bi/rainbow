package org.rainbow.asset.explorer.service.exceptions;

import org.rainbow.asset.explorer.service.exceptions.RainbowAssetExplorerException;

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
