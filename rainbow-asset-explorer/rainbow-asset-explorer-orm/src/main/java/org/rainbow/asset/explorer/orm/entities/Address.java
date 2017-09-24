package org.rainbow.asset.explorer.orm.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

import org.rainbow.orm.entities.Trackable;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Address extends Trackable<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8784885443000465066L;
	private String line1;
	private String line2;

	public Address() {
	}

	public Address(String line1, String line2, Long id) {
		super(id);
		this.line1 = line1;
		this.line2 = line2;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Override
	public Long getId() {
		return super.getId();
	}

	@Override
	public void setId(Long id) {
		super.setId(id);
	}

	public String getLine1() {
		return this.line1;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	public String getLine2() {
		return this.line2;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}
}
