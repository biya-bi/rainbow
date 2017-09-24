package org.rainbow.asset.explorer.service.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class DuplicateEmailTemplateNameException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3243856666077610104L;
	private final String name;

    public DuplicateEmailTemplateNameException(String name) {
        this(name, String.format("An email template with name '%s' already exists.", name));
    }

    public DuplicateEmailTemplateNameException(String name,  String message) {
        super(message);
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
