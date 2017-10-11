package org.rainbow.asset.explorer.service.exceptions;

import org.rainbow.asset.explorer.service.exceptions.RainbowAssetExplorerException;

/**
 *
 * @author Biya-Bi
 */
public class ProductReceiptDetailsNullOrEmptyException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7424180845028198851L;

	public ProductReceiptDetailsNullOrEmptyException() {
        super("A product receipt's details cannot be null and must contain at least one product with a positive quantity.");
    }

    public ProductReceiptDetailsNullOrEmptyException(String message) {
        super(message);
    }
}
