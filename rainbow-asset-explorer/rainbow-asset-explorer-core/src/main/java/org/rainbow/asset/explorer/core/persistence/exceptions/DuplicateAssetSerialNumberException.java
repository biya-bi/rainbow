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
public class DuplicateAssetSerialNumberException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3738386675967461518L;
	private final String serialNumber;

    public DuplicateAssetSerialNumberException(String serialNumber) {
        this(serialNumber, String.format("An asset with serial number '%s' already exists.", serialNumber));
    }

    public DuplicateAssetSerialNumberException(String SerialNumber,  String message) {
        super(message);
        this.serialNumber = SerialNumber;
    }

    public String getSerialNumber() {
        return serialNumber;
    }
}
