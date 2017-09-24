package org.rainbow.asset.explorer.service.exceptions;

/**
 * 
 * @author Biya-Bi
 */
public class DuplicateProductIssueReferenceNumberException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2934020327948484872L;
	private final String referenceNumber;

    public DuplicateProductIssueReferenceNumberException(String referenceNumber) {
        this(referenceNumber, String.format("A product issue with reference number '%s' already exists.", referenceNumber));
    }

    public DuplicateProductIssueReferenceNumberException(String referenceNumber,  String message) {
        super(message);
        this.referenceNumber = referenceNumber;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }
}
