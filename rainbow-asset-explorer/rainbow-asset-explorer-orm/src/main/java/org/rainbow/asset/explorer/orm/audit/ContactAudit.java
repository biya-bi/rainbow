package org.rainbow.asset.explorer.orm.audit;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.rainbow.asset.explorer.orm.entities.Contact;

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
