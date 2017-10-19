package org.rainbow.asset.explorer.orm.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.rainbow.orm.entities.AbstractNumericIdAuditableEntity;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Phone extends AbstractNumericIdAuditableEntity<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8194761923191622371L;
	private String line;

	public Phone() {
	}

	public Phone(String line, Long id) {
		super(id);
		this.line = line;
	}

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

	public String getLine() {
		return this.line;
	}

	public void setLine(String line) {
		this.line = line;
	}

}
