/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.audit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.rainbow.asset.explorer.core.entities.Alert;
import org.rainbow.asset.explorer.core.entities.AlertCategory;
import org.rainbow.asset.explorer.core.entities.AlertType;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "ALERT_AUDIT")
public class AlertAudit extends TrackableAudit<Alert, Integer> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 5410725596923425886L;
	private AlertCategory alertCategory;
    private AlertType alertType;
    private boolean enabled;
    private Integer scheduleId;
    private boolean immediate;

    public AlertAudit() {
    }

    public AlertAudit(Alert alert, WriteOperation writeOperation) {
        super(alert, writeOperation);
        this.alertCategory = alert.getAlertCategory();
        this.alertType = alert.getAlertType();
        this.enabled = alert.isEnabled();
        this.immediate = alert.isImmediate();
        this.scheduleId = alert.getSchedule().getId();
    }

    @NotNull
    @Column(name = "ALERT_CATEGORY", nullable = false)
    @Enumerated(EnumType.STRING)
    public AlertCategory getAlertCategory() {
        return alertCategory;
    }

    public void setAlertCategory(AlertCategory alertCategory) {
        this.alertCategory = alertCategory;
    }

    @NotNull
    @Column(name = "ALERT_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    public AlertType getAlertType() {
        return alertType;
    }

    public void setAlertType(AlertType alertType) {
        this.alertType = alertType;
    }

    @NotNull
    @Column(name = "IS_ENABLED", nullable = false)
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @NotNull
    @Column(name = "SCHEDULE_ID", nullable = false)
    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    @NotNull
    @Column(name = "IS_IMMEDIATE", nullable = false)
    public boolean isImmediate() {
        return immediate;
    }

    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.audit.AlertAudit[ auditId=" + getAuditId() + " ]";
    }

}
