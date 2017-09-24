package org.rainbow.asset.explorer.orm.audit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.rainbow.asset.explorer.orm.entities.DocumentType;
import org.rainbow.asset.explorer.orm.entities.SiteDocument;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "SITE_DOCUMENT_AUDIT")
public class SiteDocumentAudit extends TrackableAudit<SiteDocument, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8363999173647145966L;
	private String fileName;
	private String description;
	private byte[] fileContent;
	private Long siteId;
	private DocumentType documentType;

	public SiteDocumentAudit() {
	}

	public SiteDocumentAudit(SiteDocument document, WriteOperation writeOperation) {
		super(document, writeOperation);
		this.fileName = document.getFileName();
		this.description = document.getDescription();
		this.fileContent = document.getFileContent();
		this.siteId = document.getSite().getId();
		this.documentType = document.getDocumentType();
	}

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

	@Lob
	@Column(name = "FILE_CONTENT")
	public byte[] getFileContent() {
		return fileContent;
	}

	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}

	@NotNull
	@Column(name = "SITE_ID", nullable = false)
	public Long getSiteId() {
		return siteId;
	}

	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}

	@Column(name = "DOCUMENT_TYPE", nullable = false)
	@Enumerated(EnumType.STRING)
	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

	@Override
	public String toString() {
		return "org.rainbow.asset.explorer.core.audit.SiteDocumentAudit[ auditId=" + getAuditId() + " ]";
	}

}
