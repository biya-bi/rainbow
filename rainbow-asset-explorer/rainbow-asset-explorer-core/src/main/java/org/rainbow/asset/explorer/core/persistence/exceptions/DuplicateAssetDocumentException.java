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
public class DuplicateAssetDocumentException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5960399917876470307L;

	public DuplicateAssetDocumentException() {
        super("An asset can have only one document associated with it.");
    }

    public DuplicateAssetDocumentException(String message) {
        super(message);
    }
}
