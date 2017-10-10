package org.rainbow.asset.explorer.service.exceptions;

import org.rainbow.asset.explorer.service.exceptions.RainbowAssetExplorerException;

/**
 *
 * @author Biya-Bi
 */
public class DuplicateLocaleNameException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1839955764688448456L;
	private final String name;

    public DuplicateLocaleNameException(String name) {
        this(name, String.format("A locale with name '%s' already exists.", name));
    }

    public DuplicateLocaleNameException(String name,  String message) {
        super(message);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
