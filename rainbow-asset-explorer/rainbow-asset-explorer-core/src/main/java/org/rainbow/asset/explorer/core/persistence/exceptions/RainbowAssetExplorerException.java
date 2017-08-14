/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.persistence.exceptions;

import javax.ejb.ApplicationException;

/**
 *
 * @author Biya-Bi
 */
@ApplicationException(rollback = true)
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
