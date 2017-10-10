package org.rainbow.asset.explorer.service.exceptions;

import org.rainbow.asset.explorer.service.exceptions.RainbowAssetExplorerException;

/**
 *
 * @author Biya-Bi
 */
public class DuplicateCurrencyNameException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 9070329585681412755L;
	private final String name;

    public DuplicateCurrencyNameException(String name) {
        this(name, String.format("A currency with name '%s' already exists.", name));
    }

    public DuplicateCurrencyNameException(String name,  String message) {
        super(message);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
