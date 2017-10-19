package org.rainbow.security.orm.entities;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import org.rainbow.orm.entities.AbstractNumericIdAuditableEntity;

@MappedSuperclass
@Access(value = AccessType.PROPERTY)
public abstract class AbstractPolicy extends AbstractNumericIdAuditableEntity<Long> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5487398116085720071L;
	private Application application;

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

	@NotNull
	@OneToOne
	@JoinColumn(name = "APPLICATION_ID", nullable = false, unique = true)
	public Application getApplication() {
		return application;
	}

	public void setApplication(Application application) {
		this.application = application;
	}

}
