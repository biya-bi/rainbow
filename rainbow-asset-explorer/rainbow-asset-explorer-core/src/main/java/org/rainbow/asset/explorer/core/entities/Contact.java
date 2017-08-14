/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.entities;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;

import org.rainbow.asset.explorer.core.audit.AuditAdapter;
import org.rainbow.asset.explorer.core.audit.Auditable;
import org.rainbow.asset.explorer.core.audit.ContactAudit;

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

	@Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.entities.Contact[ id=" + getId() + " ]";
    }
}
