package org.rainbow.asset.explorer.service.exceptions;

import org.rainbow.asset.explorer.service.exceptions.RainbowAssetExplorerException;

/**
 *
 * @author Biya-Bi
 */
public class DuplicateVendorAccountNumberException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -5400927733690830098L;
	private final String accountNumber;

    public DuplicateVendorAccountNumberException(String accountNumber) {
        this(accountNumber, String.format("A vendor with account number '%s' already exists.", accountNumber));
    }

    public DuplicateVendorAccountNumberException(String accountNumber,  String message) {
        super(message);
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
