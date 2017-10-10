package org.rainbow.asset.explorer.faces.controllers;

import java.io.ByteArrayInputStream;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.rainbow.asset.explorer.faces.util.CrudNotificationInfo;
import org.rainbow.asset.explorer.orm.entities.SiteDocument;
import org.rainbow.asset.explorer.service.services.SiteDocumentService;
import org.rainbow.service.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@ViewScoped
@CrudNotificationInfo(createdMessageKey = "SiteDocumentCreated", updatedMessageKey = "SiteDocumentUpdated", deletedMessageKey = "SiteDocumentDeleted")
public class SiteDocumentController extends AuditableController<SiteDocument> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1950872812719298208L;

	@Autowired
	private SiteDocumentService service;

	public SiteDocumentController() {
		super(SiteDocument.class);
	}

	public void upload(FileUploadEvent event) throws Exception {
		UploadedFile file = event.getFile();

		SiteDocument current = this.getCurrent();
		current.setFileName(file.getFileName());
		current.setFileContent(file.getContents());
		current.setFileContentType(file.getContentType());
		current.setFileSize(file.getSize());
	}

	public StreamedContent getFile() {
		SiteDocument current = this.getCurrent();
		return new DefaultStreamedContent(new ByteArrayInputStream(current.getFileContent()),
				current.getFileContentType(), current.getFileName());
	}

	@Override
	protected Service<SiteDocument> getService() {
		return service;
	}
}
