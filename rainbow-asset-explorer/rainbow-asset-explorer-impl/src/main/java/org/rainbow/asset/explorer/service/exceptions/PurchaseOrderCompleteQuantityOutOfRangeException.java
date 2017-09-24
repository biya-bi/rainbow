package org.rainbow.asset.explorer.service.exceptions;

/**
 * This exception is thrown whenever an attempt is made to complete a purchase order with a negative number of products or more products
 * than ordered the purchase order.
 *
 * @author Biya-Bi
 */
public class PurchaseOrderCompleteQuantityOutOfRangeException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 8988071577340724532L;
	private final Long purchaseOrderId;
    private final Long productId;
    private final Short orderedQuantity;
    private final Short receivedQuantity;

    public PurchaseOrderCompleteQuantityOutOfRangeException(Long purchaseOrderId, Long productId, Short orderedQuantity, Short receivedQuantity) {
        this(purchaseOrderId, productId, orderedQuantity, receivedQuantity, String.format("The purchase order with ID '%s' has '%s' products with ID '%s' ordered, but the received quantity is '%s'. The received quantity for this product must be between 0 and '%s'.", purchaseOrderId, orderedQuantity, productId, receivedQuantity, orderedQuantity));
    }

    public PurchaseOrderCompleteQuantityOutOfRangeException(Long purchaseOrderId, Long productId, Short orderedQuantity, Short receivedQuantity, String message) {
        super(message);
        this.purchaseOrderId = purchaseOrderId;
        this.productId = productId;
        this.orderedQuantity = orderedQuantity;
        this.receivedQuantity = receivedQuantity;
    }

    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public Long getProductId() {
        return productId;
    }

    public Short getOrderedQuantity() {
        return orderedQuantity;
    }

    public Short getReceivedQuantity() {
        return receivedQuantity;
    }

}
