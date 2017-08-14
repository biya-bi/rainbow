/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.audit;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.rainbow.asset.explorer.core.entities.Contact;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "CONTACT_AUDIT")
public class ContactAudit extends PersonAudit<Contact> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3678740201606121339L;

	public ContactAudit() {
    }

    public ContactAudit(Contact contact, WriteOperation writeOperation) {
        super(contact, writeOperation);
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.audit.ContactAudit[ auditId=" + getAuditId() + " ]";
    }

}
