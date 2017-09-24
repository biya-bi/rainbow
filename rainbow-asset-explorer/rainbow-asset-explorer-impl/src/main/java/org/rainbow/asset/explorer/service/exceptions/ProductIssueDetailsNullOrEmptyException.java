package org.rainbow.asset.explorer.service.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class ProductIssueDetailsNullOrEmptyException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -6669724183227627672L;

	public ProductIssueDetailsNullOrEmptyException() {
        super("A product issue's details cannot be null and must contain at least one product with a positive quantity.");
    }

    public ProductIssueDetailsNullOrEmptyException(String message) {
        super(message);
    }
}
