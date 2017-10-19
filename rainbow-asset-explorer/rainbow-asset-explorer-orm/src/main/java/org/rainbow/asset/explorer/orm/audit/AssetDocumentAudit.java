package org.rainbow.asset.explorer.orm.audit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.rainbow.asset.explorer.orm.entities.AssetDocument;
import org.rainbow.asset.explorer.orm.entities.DocumentType;
import org.rainbow.orm.audit.AbstractAuditableEntityAudit;
import org.rainbow.orm.audit.WriteOperation;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "ASSET_DOCUMENT_AUDIT")
public class AssetDocumentAudit extends AbstractAuditableEntityAudit<AssetDocument, Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7626949416821766708L;
	private String fileName;
	private String description;
	private byte[] fileContent;
	private Long assetId;
	private DocumentType documentType;

	public AssetDocumentAudit() {
	}

	public AssetDocumentAudit(AssetDocument document, WriteOperation writeOperation) {
		super(document, writeOperation);
		this.fileName = document.getFileName();
		this.description = document.getDescription();
		this.fileContent = document.getFileContent();
		this.assetId = document.getAsset().getId();
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
	public Long getAssetId() {
		return assetId;
	}

	public void setAssetId(Long assetId) {
		this.assetId = assetId;
	}

	@Column(name = "DOCUMENT_TYPE", nullable = false)
	@Enumerated(EnumType.STRING)
	public DocumentType getDocumentType() {
		return documentType;
	}

	public void setDocumentType(DocumentType documentType) {
		this.documentType = documentType;
	}

}
