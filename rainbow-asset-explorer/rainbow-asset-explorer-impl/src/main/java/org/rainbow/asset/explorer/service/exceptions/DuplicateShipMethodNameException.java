package org.rainbow.asset.explorer.service.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class DuplicateShipMethodNameException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7061118978392860483L;
	private final String name;

    public DuplicateShipMethodNameException(String name) {
        this(name, String.format("A ship method with name '%s' already exists.", name));
    }

    public DuplicateShipMethodNameException(String name,  String message) {
        super(message);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
