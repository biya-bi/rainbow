package org.rainbow.asset.explorer.orm.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.rainbow.orm.entities.AbstractNumericIdAuditableEntity;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "BUSINESS_ENTITY")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class BusinessEntity extends AbstractNumericIdAuditableEntity<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 740143820664316357L;
	private List<BusinessEntityAddress> addresses;
	private List<BusinessEntityEmail> emails;
	private List<BusinessEntityFax> faxes;
	private List<BusinessEntityPhone> phones;

	public BusinessEntity() {
	}

	public BusinessEntity(Long id) {
		super(id);
	}

	public BusinessEntity(Date creationDate, Date lastUpdateDate) {
		super(creationDate, lastUpdateDate);
	}

	public BusinessEntity(Date creationDate, Date lastUpdateDate, long version, Long id) {
		super(creationDate, lastUpdateDate, version, id);
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

	@OneToMany(mappedBy = "businessEntity", cascade = CascadeType.ALL)
	public List<BusinessEntityAddress> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<BusinessEntityAddress> addresses) {
		this.addresses = addresses;
	}

	@OneToMany(mappedBy = "businessEntity", cascade = CascadeType.ALL)
	public List<BusinessEntityEmail> getEmails() {
		return emails;
	}

	public void setEmails(List<BusinessEntityEmail> emails) {
		this.emails = emails;
	}

	@OneToMany(mappedBy = "businessEntity", cascade = CascadeType.ALL)
	public List<BusinessEntityFax> getFaxes() {
		return faxes;
	}

	public void setFaxes(List<BusinessEntityFax> faxes) {
		this.faxes = faxes;
	}

	@OneToMany(mappedBy = "businessEntity", cascade = CascadeType.ALL)
	public List<BusinessEntityPhone> getPhones() {
		return phones;
	}

	public void setPhones(List<BusinessEntityPhone> phones) {
		this.phones = phones;
	}

}
