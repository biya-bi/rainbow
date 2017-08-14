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
