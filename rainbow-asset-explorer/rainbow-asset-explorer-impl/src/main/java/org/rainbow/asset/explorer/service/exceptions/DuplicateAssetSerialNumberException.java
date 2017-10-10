package org.rainbow.asset.explorer.service.exceptions;

import org.rainbow.asset.explorer.service.exceptions.RainbowAssetExplorerException;

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
