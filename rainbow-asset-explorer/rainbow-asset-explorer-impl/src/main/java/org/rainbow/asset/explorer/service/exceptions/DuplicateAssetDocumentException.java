package org.rainbow.asset.explorer.service.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class DuplicateAssetDocumentException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5960399917876470307L;

	public DuplicateAssetDocumentException() {
        super("An asset can have only one document associated with it.");
    }

    public DuplicateAssetDocumentException(String message) {
        super(message);
    }
}
