/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.entities;

import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.rainbow.asset.explorer.core.audit.AuditAdapter;
import org.rainbow.asset.explorer.core.audit.Auditable;
import org.rainbow.asset.explorer.core.audit.SiteDocumentAudit;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "SITE_DOCUMENT")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SiteDocument.findAll", query = "SELECT d FROM SiteDocument d"),
    @NamedQuery(name = "SiteDocument.findByFileName", query = "SELECT d FROM SiteDocument d WHERE UPPER(d.fileName) = :fileName"),
    @NamedQuery(name = "SiteDocument.findById", query = "SELECT d FROM SiteDocument d WHERE d.id = :id")})
@EntityListeners(AuditAdapter.class)
@Auditable(audit = SiteDocumentAudit.class)
public class SiteDocument extends Trackable<Long> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -1524631008489050475L;
	private String fileName;
    private String description;
    private byte[] fileContent;
    private Site site;
    private DocumentType documentType;
    private String fileContentType;
    private Long fileSize;

    public SiteDocument() {
    }

    public SiteDocument(Long id) {
        super(id);
    }

    public SiteDocument(String fileName) {
        this.fileName = fileName;
    }

    public SiteDocument(String fileName, Long id) {
        super(id);
        this.fileName = fileName;
    }

    public SiteDocument(String fileName, String description, Date creationDate, Date lastUpdateDate) {
        super(creationDate, lastUpdateDate);
        this.fileName = fileName;
        this.description = description;
    }

    public SiteDocument(String fileName, String description, Date creationDate, Date lastUpdateDate, long version, Long id) {
        super(creationDate, lastUpdateDate, version, id);
        this.fileName = fileName;
        this.description = description;
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

    @Basic(optional = false)
    @NotNull
    @Size(min = 1)
    @Column(name = "FILE_NAME", nullable = false)
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NotNull
    @Lob
    @Column(name = "FILE_CONTENT",nullable = false)
    public byte[] getFileContent() {
        return fileContent;
    }

    public void setFileContent(byte[] fileContent) {
        this.fileContent = fileContent;
    }

    @NotNull
    @ManyToOne
    @JoinColumn(name = "SITE_ID", nullable = false)
    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    @Column(name = "DOCUMENT_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    public DocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(DocumentType documentType) {
        this.documentType = documentType;
    }

    @NotNull
    @Column(name = "FILE_CONTENT_TYPE", nullable = false)
    public String getFileContentType() {
        return fileContentType;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    @NotNull
    @Column(name = "FILE_SIZE", nullable = false)
    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
        return "org.rainbow.asset.explorer.core.entities.SiteDocument[ id=" + getId() + " ]";
    }

}
