package org.rainbow.asset.explorer.core.persistence.exceptions;

public class EntityNotFoundException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 6487925782589776073L;

	public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
