package org.rainbow.journal.server.dto;

import java.util.Date;

public class FileDto extends AuditableDto<Long> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1620287025835862647L;
	private String fileName;
	private byte[] fileContent;
	private String fileContentType;
	private Long fileSize;

	public FileDto() {
	}

	public FileDto(Long id) {
		super(id);
	}

	public FileDto(String fileName, byte[] fileContent, String fileContentType, Long fileSize, String creator,
			String updater, Date creationDate, Date lastUpdateDate, long version, Long id) {
		super(creator, updater, creationDate, lastUpdateDate, version, id);
		this.fileName = fileName;
		this.fileContent = fileContent;
		this.fileContentType = fileContentType;
		this.fileSize = fileSize;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public byte[] getFileContent() {
		return fileContent;
	}

	public void setFileContent(byte[] fileContent) {
		this.fileContent = fileContent;
	}

	public String getFileContentType() {
		return fileContentType;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}

}
