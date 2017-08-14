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
