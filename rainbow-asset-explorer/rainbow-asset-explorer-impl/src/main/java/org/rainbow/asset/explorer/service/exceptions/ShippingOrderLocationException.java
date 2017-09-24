package org.rainbow.asset.explorer.service.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class ShippingOrderLocationException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 9041375056764177027L;
	private final Long locationId;

    public ShippingOrderLocationException( Long locationId) {
        this( locationId, (Throwable) null);
    }

    public ShippingOrderLocationException(Long locationId, String message) {
        this(locationId, message, null);
    }

    public ShippingOrderLocationException( Long locationId, Throwable cause) {
        this(locationId, String.format("The location with ID '%s' has been specified as the source and target location of a shipping order. The source and target locations in a shipping order must be different.", locationId), cause);
    }

    public ShippingOrderLocationException( Long locationId, String message, Throwable cause) {
        super(message, cause);
        this.locationId = locationId;
    }

    public Long getLocationId() {
        return locationId;
    }
}
