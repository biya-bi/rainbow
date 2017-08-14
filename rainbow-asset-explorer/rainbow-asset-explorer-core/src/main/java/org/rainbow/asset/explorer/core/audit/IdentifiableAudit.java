/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.audit;

import java.io.Serializable;
import javax.persistence.MappedSuperclass;

import org.rainbow.asset.explorer.core.entities.Identifiable;

/**
 *
 * @author Biya-Bi
 * @param <TEntity>
 * @param <TEntityId>
 */
@MappedSuperclass
public abstract class IdentifiableAudit<TEntity extends Identifiable<TEntityId>, TEntityId extends Serializable> extends Audit {

    /**
	 * 
	 */
	private static final long serialVersionUID = 2621119435306003800L;
	private TEntityId id;

    public IdentifiableAudit() {
    }

    public IdentifiableAudit(TEntity entity, WriteOperation writeOperation) {
        super(writeOperation);
        this.id = entity.getId();
    }

    public TEntityId getId() {
        return id;
    }

    public void setId(TEntityId id) {
        this.id = id;
    }

}
