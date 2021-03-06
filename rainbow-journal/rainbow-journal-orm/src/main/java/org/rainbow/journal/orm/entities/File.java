package org.rainbow.journal.orm.entities;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.rainbow.orm.entities.AbstractNumericIdAuditableEntity;

@Entity
@Access(value = AccessType.PROPERTY)
public class File extends AbstractNumericIdAuditableEntity<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8624422391603386298L;

	private String fileName;
	private byte[] fileContent;
	private String fileContentType;
	private Long fileSize;

	public File() {
	}

	public File(Long id) {
		super(id);
	}

	public File(String fileName, byte[] fileContent, String fileContentType, Long fileSize) {
		super();
		this.fileName = fileName;
		this.fileContent = fileContent;
		this.fileContentType = fileContentType;
		this.fileSize = fileSize;
	}

	public File(String fileName, byte[] fileContent, String fileContentType, Long fileSize, String creator,
			String updater, Date creationDate, Date lastUpdateDate, long version, Long id) {
		super(creator, updater, creationDate, lastUpdateDate, version, id);
		this.fileName = fileName;
		this.fileContent = fileContent;
		this.fileContentType = fileContentType;
		this.fileSize = fileSize;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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

	@NotNull
	@Lob
	@Column(name = "FILE_CONTENT", length = Integer.MAX_VALUE, nullable = false)
	public byte[] getFileContent() {
		return fileContent;
	}

	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
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

}
