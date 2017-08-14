/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.persistence.exceptions;

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
