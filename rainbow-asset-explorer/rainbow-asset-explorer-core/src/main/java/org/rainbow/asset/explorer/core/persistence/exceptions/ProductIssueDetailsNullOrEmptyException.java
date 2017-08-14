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
