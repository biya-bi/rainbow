package org.rainbow.asset.explorer.service.exceptions;

import org.rainbow.asset.explorer.service.exceptions.RainbowAssetExplorerException;

/**
 *
 * @author Biya-Bi
 */
public class DuplicateEmailRecipientEmailException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5112859705370514586L;
	private final String email;

    public DuplicateEmailRecipientEmailException(String email) {
        this(email, String.format("An email recipient with email '%s' already exists.", email));
    }

    public DuplicateEmailRecipientEmailException(String name,  String message) {
        super(message);
        this.email = name;
    }

    public String getEmail() {
        return email;
    }
}
