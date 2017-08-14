/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.faces.controllers;

import static org.rainbow.asset.explorer.faces.utilities.ResourceBundles.CRUD_MESSAGES;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.rainbow.asset.explorer.core.entities.AddressType;
import org.rainbow.asset.explorer.core.entities.BusinessEntityAddress;
import org.rainbow.asset.explorer.core.entities.BusinessEntityEmail;
import org.rainbow.asset.explorer.core.entities.BusinessEntityFax;
import org.rainbow.asset.explorer.core.entities.BusinessEntityPhone;
import org.rainbow.asset.explorer.core.entities.EmailType;
import org.rainbow.asset.explorer.core.entities.FaxType;
import org.rainbow.asset.explorer.core.entities.PhoneType;
import org.rainbow.asset.explorer.core.entities.Vendor;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicateVendorAccountNumberException;
import org.rainbow.asset.explorer.faces.utilities.CrudNotificationInfo;
import org.rainbow.asset.explorer.faces.utilities.JsfUtil;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@ViewScoped
@CrudNotificationInfo(createdMessageKey = "VendorCreated", updatedMessageKey = "VendorUpdated", deletedMessageKey = "VendorDeleted")
public class VendorController extends TrackableController<Vendor, Long, SearchOptions> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5367612890139346845L;

	private static final String DUPLICATE_VENDOR_ACCOUNT_NUMBER_ERROR_KEY = "DuplicateVendorAccountNumber";

	@Autowired
	@Qualifier("vendorService")
	private Service<Vendor, Long, SearchOptions> service;

	public VendorController() {
		super(Vendor.class);
	}

	@Override
	public Vendor prepareCreate() {
		Vendor vendor = super.prepareCreate();

		List<BusinessEntityAddress> contactAddresses = new ArrayList<>();
		contactAddresses.add(new BusinessEntityAddress(AddressType.BUSINESS));
		contactAddresses.add(new BusinessEntityAddress(AddressType.HOME));
		contactAddresses.add(new BusinessEntityAddress(AddressType.BILLING));
		contactAddresses.add(new BusinessEntityAddress(AddressType.SHIPPING));
		contactAddresses.add(new BusinessEntityAddress(AddressType.OTHER));
		vendor.setAddresses(contactAddresses);

		List<BusinessEntityPhone> contactPhones = new ArrayList<>();
		contactPhones.add(new BusinessEntityPhone(PhoneType.BUSINESS));
		contactPhones.add(new BusinessEntityPhone(PhoneType.HOME));
		contactPhones.add(new BusinessEntityPhone(PhoneType.MOBILE));
		contactPhones.add(new BusinessEntityPhone(PhoneType.OTHER));
		vendor.setPhones(contactPhones);

		List<BusinessEntityFax> contactFaxes = new ArrayList<>();
		contactFaxes.add(new BusinessEntityFax(FaxType.BUSINESS));
		contactFaxes.add(new BusinessEntityFax(FaxType.HOME));
		contactFaxes.add(new BusinessEntityFax(FaxType.OTHER));
		vendor.setFaxes(contactFaxes);

		List<BusinessEntityEmail> contactEmails = new ArrayList<>();
		contactEmails.add(new BusinessEntityEmail(EmailType.EMAIL1));
		contactEmails.add(new BusinessEntityEmail(EmailType.EMAIL2));
		contactEmails.add(new BusinessEntityEmail(EmailType.EMAIL3));
		vendor.setEmails(contactEmails);

		return vendor;
	}

	@Override
	protected boolean handle(Throwable throwable) {
		if (throwable instanceof DuplicateVendorAccountNumberException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			DuplicateVendorAccountNumberException e = (DuplicateVendorAccountNumberException) throwable;
			JsfUtil.addErrorMessage(String.format(
					ResourceBundle.getBundle(CRUD_MESSAGES).getString(DUPLICATE_VENDOR_ACCOUNT_NUMBER_ERROR_KEY),
					e.getAccountNumber()));
			return true;
		}
		return super.handle(throwable);
	}

	@Override
	protected Service<Vendor, Long, SearchOptions> getService() {
		return service;
	}
}
