package org.rainbow.asset.explorer.orm.entities;

import javax.persistence.Entity;

import org.rainbow.asset.explorer.orm.audit.ContactAudit;
import org.rainbow.orm.audit.Auditable;

/**
 *
 * @author Biya-Bi
 */
@Entity

@Auditable(ContactAudit.class)
public class Contact extends Person {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8164310947154663941L;

}
