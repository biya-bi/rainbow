package org.rainbow.asset.explorer.service.exceptions;

import org.rainbow.asset.explorer.orm.entities.PurchaseOrderStatus;
import org.rainbow.asset.explorer.service.exceptions.RainbowAssetExplorerException;

/**
 * This exception is thrown whenever the transition of the status of a purchase
 * order is not valid.
 *
 * @author Biya-Bi
 */
public class PurchaseOrderStatusTransitionException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8139913831775017040L;
	private final PurchaseOrderStatus sourceStatus;
    private final PurchaseOrderStatus targetStatus;

    public PurchaseOrderStatusTransitionException(PurchaseOrderStatus sourceStatus, PurchaseOrderStatus targetStatus) {
        this(sourceStatus, targetStatus, String.format("The status of a purchase order cannot be transited from %s to %s.", sourceStatus, targetStatus));
    }

    public PurchaseOrderStatusTransitionException(PurchaseOrderStatus sourceStatus, PurchaseOrderStatus targetStatus, String message) {
        super(message);
        this.sourceStatus = sourceStatus;
        this.targetStatus = targetStatus;
    }

    public PurchaseOrderStatus getSourceStatus() {
        return sourceStatus;
    }

    public PurchaseOrderStatus getTargetStatus() {
        return targetStatus;
    }
    
}
