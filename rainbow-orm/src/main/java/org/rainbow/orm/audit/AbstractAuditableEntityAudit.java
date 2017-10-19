package org.rainbow.orm.audit;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

import org.rainbow.orm.entities.AbstractAuditableEntity;

/**
 *
 * @author Biya-Bi
 * @param <TEntity>
 * @param <TId>
 */
@MappedSuperclass
public abstract class AbstractAuditableEntityAudit<TEntity extends AbstractAuditableEntity<TId>, TId extends Serializable> extends AbstractAudit<TEntity> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4929766740970109632L;
	private TId id;

	public AbstractAuditableEntityAudit() {
	}

	public AbstractAuditableEntityAudit(TEntity entity, WriteOperation writeOperation) {
		super(entity, writeOperation, entity.getCreationDate(), entity.getLastUpdateDate(), entity.getCreator(),
				entity.getUpdater(), entity.getVersion());
		this.id = entity.getId();
	}

	public TId getId() {
		return id;
	}

	public void setId(TId id) {
		this.id = id;
	}

}
