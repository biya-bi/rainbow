/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.entities;

import java.util.Date;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.rainbow.asset.explorer.core.audit.AlertAudit;
import org.rainbow.asset.explorer.core.audit.AuditAdapter;
import org.rainbow.asset.explorer.core.audit.Auditable;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(columnNames = {"ALERT_TYPE", "ALERT_CATEGORY"})})
@EntityListeners(AuditAdapter.class)
@Auditable(audit = AlertAudit.class)
public class Alert extends Trackable<Integer> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7305270011781967831L;
	private AlertCategory alertCategory;
    private AlertType alertType;
    private boolean enabled;
    private Schedule schedule;
    private boolean immediate;
    private List<EmailTemplate> emailTemplates;
    private List<EmailRecipient> emailRecipients;

    public Alert() {
    }

    public Alert(Integer id) {
        super(id);
    }

    public Alert(AlertCategory alertCategory, AlertType alertType, boolean enabled) {
        this.alertCategory = alertCategory;
        this.alertType = alertType;
        this.enabled = enabled;
    }

    public Alert(AlertCategory alertCategory, AlertType alertType, boolean enabled, boolean immediate, Schedule schedule, String createdBy, String updatedBy, Date creationDate, Date lastUpdateDate, long version, Integer id) {
        super(createdBy, updatedBy, creationDate, lastUpdateDate, version, id);
        this.alertCategory = alertCategory;
        this.alertType = alertType;
        this.enabled = enabled;
        this.immediate = immediate;
        this.schedule = schedule;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Override
    public Integer getId() {
        return super.getId();
    }

    @Override
    public void setId(Integer id) {
        super.setId(id);
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
    @JoinColumn(name = "SCHEDULE_ID", nullable = false)
    @ManyToOne(cascade = CascadeType.ALL)
    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    @NotNull
    @Column(name = "IS_IMMEDIATE", nullable = false)
    public boolean isImmediate() {
        return immediate;
    }

    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }

    @ManyToMany
    @JoinTable(name = "ALERT_EMAIL_TEMPLATE", joinColumns = {
        @JoinColumn(name = "ALERT_ID")}, inverseJoinColumns = {
        @JoinColumn(name = "EMAIL_TEMPLATE_ID")})
    public List<EmailTemplate> getEmailTemplates() {
        return emailTemplates;
    }

    public void setEmailTemplates(List<EmailTemplate> emailTemplates) {
        this.emailTemplates = emailTemplates;
    }

    @ManyToMany
    @JoinTable(name = "ALERT_EMAIL_RECIPIENT", joinColumns = {
        @JoinColumn(name = "ALERT_ID")}, inverseJoinColumns = {
        @JoinColumn(name = "EMAIL_RECIPIENT_ID")})
    public List<EmailRecipient> getEmailRecipients() {
        return emailRecipients;
    }

    public void setEmailRecipients(List<EmailRecipient> emailRecipients) {
        this.emailRecipients = emailRecipients;
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.entities.Alert[ id=" + getId() + " ]";
    }
}
