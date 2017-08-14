/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.audit;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.rainbow.asset.explorer.core.entities.Trackable;

/**
 *
 * @author Biya-Bi
 * @param <TEntity>
 * @param <TEntityId>
 */
@MappedSuperclass
public abstract class TrackableAudit<TEntity extends Trackable<TEntityId>, TEntityId extends Serializable> extends IdentifiableAudit<TEntity, TEntityId> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4929766740970109632L;
	private Date creationDate;
    private Date lastUpdateDate;
    private String creator;
    private String updater;
    private Long version;

    public TrackableAudit() {
    }

    public TrackableAudit(TEntity entity, WriteOperation writeOperation) {
        super(entity, writeOperation);
        this.creationDate = entity.getCreationDate();
        this.lastUpdateDate = entity.getLastUpdateDate();
        this.creator = entity.getCreator();
        this.updater = entity.getUpdater();
        this.version = entity.getVersion();
    }

    @Column
    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getUpdater() {
        return updater;
    }

    public void setUpdater(String updater) {
        this.updater = updater;
    }

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATION_DATE", nullable = false)
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_UPDATE_DATE", nullable = false)
    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Long getVersion() {
        return this.version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
