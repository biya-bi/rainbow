/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.persistence.exceptions;

/**
 * This exception is thrown whenever an attempt is made to deliver a negative number of products or more products
 * than shipped in a shipping order.
 *
 * @author Biya-Bi
 */
public class ShippingOrderDeliveredQuantityOutOfRangeException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1249687886704960624L;
	private final Long shippingOrderId;
    private final Long productId;
    private final Short shippedQuantity;
    private final Short deliveredQuantity;

    public ShippingOrderDeliveredQuantityOutOfRangeException(Long shippingOrderId, Long productId, Short shippedQuantity, Short deliveredQuantity) {
        this(shippingOrderId, productId, shippedQuantity, deliveredQuantity, String.format("The shipping order with ID '%s' has '%s' products with ID '%s' shipped, but the delivered quantity is '%s'. The delivered quantity for this product must be between 0 and '%s'.", shippingOrderId, shippedQuantity, productId, deliveredQuantity, shippedQuantity));
    }

    public ShippingOrderDeliveredQuantityOutOfRangeException(Long shippingOrderId, Long productId, Short shippedQuantity, Short deliveredQuantity, String message) {
        super(message);
        this.shippingOrderId = shippingOrderId;
        this.productId = productId;
        this.shippedQuantity = shippedQuantity;
        this.deliveredQuantity = deliveredQuantity;
    }

    public Long getShippingOrderId() {
        return shippingOrderId;
    }

    public Long getProductId() {
        return productId;
    }

    public Short getShippedQuantity() {
        return shippedQuantity;
    }

    public Short getDeliveredQuantity() {
        return deliveredQuantity;
    }

}
