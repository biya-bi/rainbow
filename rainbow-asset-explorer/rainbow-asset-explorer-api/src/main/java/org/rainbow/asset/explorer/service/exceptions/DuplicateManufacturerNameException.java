package org.rainbow.asset.explorer.service.exceptions;

import org.rainbow.asset.explorer.service.exceptions.RainbowAssetExplorerException;

/**
 *
 * @author Biya-Bi
 */
public class DuplicateManufacturerNameException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3262300196825855699L;
	private final String name;

    public DuplicateManufacturerNameException(String name) {
        this(name, String.format("A manufacturer with name '%s' already exists.", name));
    }

    public DuplicateManufacturerNameException(String name,  String message) {
        super(message);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
