package org.rainbow.asset.explorer.service.exceptions;

import org.rainbow.asset.explorer.service.exceptions.RainbowAssetExplorerException;

/**
 *
 * @author Biya-Bi
 */
public class DuplicateAssetTypeNameException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2458210784347060454L;
	private final String name;

    public DuplicateAssetTypeNameException(String name) {
        this(name, String.format("An asset type with name '%s' already exists.", name));
    }

    public DuplicateAssetTypeNameException(String name,  String message) {
        super(message);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
