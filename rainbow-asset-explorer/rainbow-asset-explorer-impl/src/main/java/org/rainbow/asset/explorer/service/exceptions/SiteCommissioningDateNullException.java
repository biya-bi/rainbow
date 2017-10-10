package org.rainbow.asset.explorer.service.exceptions;

import org.rainbow.asset.explorer.service.exceptions.RainbowAssetExplorerException;

/**
 *
 * @author Biya-Bi
 */
public class SiteCommissioningDateNullException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1549197037801067721L;

	public SiteCommissioningDateNullException() {
        super("An active site must have a commissioning date.");
    }

    public SiteCommissioningDateNullException(String message) {
        super(message);
    }
}
