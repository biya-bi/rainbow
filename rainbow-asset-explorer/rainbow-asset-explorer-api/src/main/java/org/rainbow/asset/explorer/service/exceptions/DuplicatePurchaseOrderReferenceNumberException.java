package org.rainbow.asset.explorer.service.exceptions;

import org.rainbow.asset.explorer.service.exceptions.RainbowAssetExplorerException;

/**
 * 
 * @author Biya-Bi
 */
public class DuplicatePurchaseOrderReferenceNumberException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2980033774666558200L;
	private final String referenceNumber;

    public DuplicatePurchaseOrderReferenceNumberException(String referenceNumber) {
        this(referenceNumber, String.format("A purchase order with reference number '%s' already exists.", referenceNumber));
    }

    public DuplicatePurchaseOrderReferenceNumberException(String referenceNumber,  String message) {
        super(message);
        this.referenceNumber = referenceNumber;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }
}
