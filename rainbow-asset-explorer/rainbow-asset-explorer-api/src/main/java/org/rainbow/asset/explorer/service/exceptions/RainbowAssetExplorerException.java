package org.rainbow.asset.explorer.service.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class RainbowAssetExplorerException extends RuntimeException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2302908330419228601L;

	public RainbowAssetExplorerException() {
    }

    public RainbowAssetExplorerException(String message) {
        super(message);
    }

    public RainbowAssetExplorerException(String message, Throwable cause) {
        super(message, cause);
    }

    public RainbowAssetExplorerException(Throwable cause) {
        super(cause);
    }

    public RainbowAssetExplorerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
