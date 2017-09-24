package org.rainbow.asset.explorer.service.exceptions;

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
