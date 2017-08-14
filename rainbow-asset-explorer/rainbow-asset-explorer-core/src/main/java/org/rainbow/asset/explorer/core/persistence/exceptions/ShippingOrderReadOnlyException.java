/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.persistence.exceptions;

import org.rainbow.asset.explorer.core.entities.ShippingOrderStatus;

/**
 * This exception is thrown whenever an attempt is made to update or deleted a
 * shipping order that is not pending.
 *
 * @author Biya-Bi
 */
public class ShippingOrderReadOnlyException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1318483540486897581L;
	private final ShippingOrderStatus status;

    public ShippingOrderReadOnlyException(ShippingOrderStatus status) {
        this(status, "A shipping order that is not pending can neither be modified nor deleted.");
    }

    public ShippingOrderReadOnlyException(ShippingOrderStatus status, String message) {
        super(message);
        this.status = status;
    }

    public ShippingOrderStatus getStatus() {
        return status;
    }

}
