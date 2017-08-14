/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.persistence.exceptions;

import org.rainbow.asset.explorer.core.entities.ShippingOrderStatus;

/**
 * This exception is thrown whenever the transition of the status of a shipping
 * order is not valid.
 *
 * @author Biya-Bi
 */
public class ShippingOrderStatusTransitionException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 541713937543542680L;
	private final ShippingOrderStatus sourceStatus;
    private final ShippingOrderStatus targetStatus;

    public ShippingOrderStatusTransitionException(ShippingOrderStatus sourceStatus, ShippingOrderStatus targetStatus) {
        this(sourceStatus, targetStatus, String.format("The status of a shipping order cannot be transited from %s to %s.", sourceStatus, targetStatus));
    }

    public ShippingOrderStatusTransitionException(ShippingOrderStatus sourceStatus, ShippingOrderStatus targetStatus, String message) {
        super(message);
        this.sourceStatus = sourceStatus;
        this.targetStatus = targetStatus;
    }

    public ShippingOrderStatus getSourceStatus() {
        return sourceStatus;
    }

    public ShippingOrderStatus getTargetStatus() {
        return targetStatus;
    }
    
}
