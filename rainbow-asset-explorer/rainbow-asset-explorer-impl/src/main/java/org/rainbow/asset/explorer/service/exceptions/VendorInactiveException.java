package org.rainbow.asset.explorer.service.exceptions;

/**
 * This exception is thrown whenever an inactive vendor is specified
 * for a purchase order that is pending.
 *
 * @author Biya-Bi
 */
public class VendorInactiveException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7608103102650164360L;

	public VendorInactiveException() {
        this("An inactive vendor cannot be specified for a purchase order that is pending.");
    }

    public VendorInactiveException(String message) {
        super(message);
    }
}
