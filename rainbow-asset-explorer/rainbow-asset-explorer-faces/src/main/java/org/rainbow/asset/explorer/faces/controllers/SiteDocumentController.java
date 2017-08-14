/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.faces.controllers;

import java.io.ByteArrayInputStream;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.rainbow.asset.explorer.core.entities.SiteDocument;
import org.rainbow.asset.explorer.faces.utilities.CrudNotificationInfo;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@ViewScoped
@CrudNotificationInfo(createdMessageKey = "SiteDocumentCreated", updatedMessageKey = "SiteDocumentUpdated", deletedMessageKey = "SiteDocumentDeleted")
public class SiteDocumentController extends TrackableController<SiteDocument, Long, SearchOptions> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1950872812719298208L;

	@Autowired
	@Qualifier("siteDocumentService")
	private Service<SiteDocument, Long, SearchOptions> service;

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
	protected Service<SiteDocument, Long, SearchOptions> getService() {
		return service;
	}
}
