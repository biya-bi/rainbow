package org.rainbow.asset.explorer.service.exceptions;

/**
 * This exception is thrown whenever an attempt is made to retrieve products
 * from an inventory that does not have enough products.
 *
 * @author Biya-Bi
 */
public class InsufficientInventoryException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3568696091565119457L;
	private final Long locationId;
    private final Long productId;
    private final Short availableQuantity;
    private final Short requestedQuantity;

    public InsufficientInventoryException(Long locationId, Long productId, Short availableQuantity, Short requestedQuantity) {
        this(locationId, productId, availableQuantity, requestedQuantity, (Throwable) null);
    }

    public InsufficientInventoryException(Long locationId, Long productId, Short availableQuantity, Short requestedQuantity, Throwable cause) {
        this(locationId, productId, availableQuantity, requestedQuantity, String.format("The location with ID '%s' currently has '%s' products with ID '%s' in the inventory, but the requested quantity is '%s'.", locationId, availableQuantity, productId, requestedQuantity), cause);
    }

    public InsufficientInventoryException(Long locationId, Long productId, Short availableQuantity, Short requestedQuantity, String message) {
        this(locationId, productId, availableQuantity, requestedQuantity, message, null);
    }

    public InsufficientInventoryException(Long locationId, Long productId, Short availableQuantity, Short requestedQuantity, String message, Throwable cause) {
        super(message, cause);
        this.locationId = locationId;
        this.productId = productId;
        this.availableQuantity = availableQuantity;
        this.requestedQuantity = requestedQuantity;
    }

    public Long getLocationId() {
        return locationId;
    }

    public Long getProductId() {
        return productId;
    }

    public Short getAvailableQuantity() {
        return availableQuantity;
    }

    public Short getRequestedQuantity() {
        return requestedQuantity;
    }

}
