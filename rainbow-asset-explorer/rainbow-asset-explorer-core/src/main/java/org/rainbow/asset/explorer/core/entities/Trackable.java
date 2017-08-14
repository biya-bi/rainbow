/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Biya-Bi
 * @param <T> The type of the Id
 */
@MappedSuperclass
public abstract class Trackable<T extends Serializable> extends Identifiable<T> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 291910680942644416L;
	private String creator;
    private String updater;
    private Date creationDate;
    private Date lastUpdateDate;
    private long version;

    public Trackable() {
    }

    public Trackable(T id) {
        super(id);
    }

    public Trackable(Date creationDate, Date lastUpdateDate) {
        this.creationDate = creationDate;
        this.lastUpdateDate = lastUpdateDate;
    }

    public Trackable(Date creationDate, Date lastUpdateDate, long version, T id) {
        super(id);
        this.creationDate = creationDate;
        this.lastUpdateDate = lastUpdateDate;
        this.version = version;
    }

    public Trackable(String creator, String updater, Date creationDate, Date lastUpdateDate) {
        this.creator = creator;
        this.updater = updater;
        this.creationDate = creationDate;
        this.lastUpdateDate = lastUpdateDate;
    }

    public Trackable(String creator, String updater, Date creationDate, Date lastUpdateDate, long version, T id) {
        super(id);
        this.creator = creator;
        this.updater = updater;
        this.creationDate = creationDate;
        this.lastUpdateDate = lastUpdateDate;
        this.version = version;
    }

    @Column(updatable = false)
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

    @Version
    public long getVersion() {
        return this.version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @PrePersist
    private void onPersist() {
        lastUpdateDate = creationDate = new Date();
    }

    @PreUpdate
    private void onUpdate() {
        lastUpdateDate = new Date();
    }

//    @PreRemove
//    private void onDelete() {
//        lastUpdateDate = new Date();
//    }
}
