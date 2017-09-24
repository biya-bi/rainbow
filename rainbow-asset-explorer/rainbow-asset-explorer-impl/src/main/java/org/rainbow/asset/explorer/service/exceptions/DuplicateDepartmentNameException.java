package org.rainbow.asset.explorer.service.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class DuplicateDepartmentNameException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3030780847531876335L;
	private final String name;

    public DuplicateDepartmentNameException(String name) {
        this(name, String.format("A department with name '%s' already exists.", name));
    }

    public DuplicateDepartmentNameException(String name,  String message) {
        super(message);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
