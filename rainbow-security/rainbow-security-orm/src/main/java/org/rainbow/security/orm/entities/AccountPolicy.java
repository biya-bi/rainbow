package org.rainbow.security.orm.entities;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Access(value = AccessType.PROPERTY)
public abstract class AccountPolicy implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4920232754764541095L;

}
