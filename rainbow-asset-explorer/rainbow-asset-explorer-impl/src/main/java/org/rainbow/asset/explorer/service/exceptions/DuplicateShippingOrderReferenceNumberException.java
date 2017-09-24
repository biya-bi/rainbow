package org.rainbow.asset.explorer.service.exceptions;

/**
 * 
 * @author Biya-Bi
 */
public class DuplicateShippingOrderReferenceNumberException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1479503260572089559L;
	private final String referenceNumber;

    public DuplicateShippingOrderReferenceNumberException(String referenceNumber) {
        this(referenceNumber, String.format("A shipping order with reference number '%s' already exists.", referenceNumber));
    }

    public DuplicateShippingOrderReferenceNumberException(String referenceNumber,  String message) {
        super(message);
        this.referenceNumber = referenceNumber;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }
}
