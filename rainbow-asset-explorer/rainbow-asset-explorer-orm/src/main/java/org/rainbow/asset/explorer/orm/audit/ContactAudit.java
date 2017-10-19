package org.rainbow.asset.explorer.orm.audit;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.rainbow.asset.explorer.orm.entities.Contact;
import org.rainbow.orm.audit.WriteOperation;

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

}
