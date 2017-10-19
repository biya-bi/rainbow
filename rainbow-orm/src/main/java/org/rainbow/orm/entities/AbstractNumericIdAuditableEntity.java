package org.rainbow.orm.entities;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author Biya-Bi
 * @param <T>
 *            The type of the ID
 */
@MappedSuperclass
@Access(value = AccessType.PROPERTY)
public abstract class AbstractNumericIdAuditableEntity<T extends Number> extends AbstractAuditableEntity<T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2779807034300413801L;

	public AbstractNumericIdAuditableEntity() {
	}

	public AbstractNumericIdAuditableEntity(T id) {
		super(id);
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Override
	public T getId() {
		return super.getId();
	}

	@Override
	public void setId(T id) {
		super.setId(id);
	}

	public AbstractNumericIdAuditableEntity(Date creationDate, Date lastUpdateDate, long version, T id) {
		super(creationDate, lastUpdateDate, version, id);
	}

	public AbstractNumericIdAuditableEntity(Date creationDate, Date lastUpdateDate) {
		super(creationDate, lastUpdateDate);
	}

	public AbstractNumericIdAuditableEntity(String creator, String updater, Date creationDate, Date lastUpdateDate, long version,
			T id) {
		super(creator, updater, creationDate, lastUpdateDate, version, id);
	}

	public AbstractNumericIdAuditableEntity(String creator, String updater, Date creationDate, Date lastUpdateDate) {
		super(creator, updater, creationDate, lastUpdateDate);
	}

}
