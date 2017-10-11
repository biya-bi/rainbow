package org.rainbow.asset.explorer.service.exceptions;

import org.rainbow.asset.explorer.orm.entities.PurchaseOrderStatus;
import org.rainbow.asset.explorer.service.exceptions.RainbowAssetExplorerException;

/**
 * This exception is thrown whenever an attempt is made to update or deleted a
 * shipping order that is not pending.
 *
 * @author Biya-Bi
 */
public class PurchaseOrderReadOnlyException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4000325700703085767L;
	private final PurchaseOrderStatus status;

    public PurchaseOrderReadOnlyException(PurchaseOrderStatus status) {
        this(status, "A purchase order that is not pending can neither be modified nor deleted.");
    }

    public PurchaseOrderReadOnlyException(PurchaseOrderStatus status, String message) {
        super(message);
        this.status = status;
    }

    public PurchaseOrderStatus getStatus() {
        return status;
    }

}
