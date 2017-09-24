package org.rainbow.asset.explorer.orm.entities;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;

import org.rainbow.asset.explorer.orm.adapters.AuditAdapter;
import org.rainbow.asset.explorer.orm.audit.Auditable;
import org.rainbow.asset.explorer.orm.audit.ContactAudit;

/**
 *
 * @author Biya-Bi
 */
@Entity
@EntityListeners(AuditAdapter.class)
@Auditable(audit = ContactAudit.class)
public class Contact extends Person {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8164310947154663941L;

}
