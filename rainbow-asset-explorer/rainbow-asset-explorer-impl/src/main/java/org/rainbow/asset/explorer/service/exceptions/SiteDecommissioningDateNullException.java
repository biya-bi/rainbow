package org.rainbow.asset.explorer.service.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class SiteDecommissioningDateNullException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3067331271557423592L;

	public SiteDecommissioningDateNullException() {
        super("A decommissioned site must have a decommissioning date.");
    }

    public SiteDecommissioningDateNullException(String message) {
        super(message);
    }
}
