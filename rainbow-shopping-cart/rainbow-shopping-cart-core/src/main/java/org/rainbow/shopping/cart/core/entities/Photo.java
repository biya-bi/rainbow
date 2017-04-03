package org.rainbow.shopping.cart.core.entities;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.rainbow.core.entities.Trackable;

@Entity
@Table(name = "Photos")
@Access(value = AccessType.PROPERTY)
public class Photo extends Trackable<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8624422391603386298L;

	private String fileName;
	private byte[] fileContent;
	private String fileContentType;
	private Long fileSize;

	public Photo() {
	}

	public Photo(String fileName, byte[] fileContent, String fileContentType, Long fileSize) {
		super();
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
