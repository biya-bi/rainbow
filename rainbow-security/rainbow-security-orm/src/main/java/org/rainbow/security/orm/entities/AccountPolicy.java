package org.rainbow.security.orm.entities;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
@Access(value = AccessType.PROPERTY)
public abstract class AccountPolicy extends AbstractPolicy {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4920232754764541095L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Override
	public Long getId() {
		return super.getId();
	}

	@Override
	public void setId(Long id) {
		super.setId(id);
	}
}
