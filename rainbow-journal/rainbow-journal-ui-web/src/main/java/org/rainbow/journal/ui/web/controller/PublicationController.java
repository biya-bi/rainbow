/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.journal.ui.web.controller;

import java.io.IOException;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.journal.core.entities.File;
import org.rainbow.journal.core.entities.Publication;
import org.rainbow.journal.ui.web.utilities.BytesToImageConverter;
import org.rainbow.journal.ui.web.utilities.CrudNotificationInfo;
import org.rainbow.journal.ui.web.utilities.ProfileHelper;
import org.rainbow.service.Service;
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
@CrudNotificationInfo(createdMessageKey = "PublicationCreated", updatedMessageKey = "PublicationUpdated", deletedMessageKey = "PublicationDeleted")
public class PublicationController extends TrackableController<Publication, Long, SearchOptions> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8860697631924881525L;

	@Autowired
	@Qualifier("publicationService")
	private Service<Publication, Long, SearchOptions> service;

	@Autowired
	private BytesToImageConverter bytesToImageConverter;

	@Autowired
	private ProfileHelper profileHelper;

	public PublicationController() {
		super(Publication.class);
	}

	@Override
	protected Service<Publication, Long, SearchOptions> getService() {
		return service;
	}

	public void uploadFile(FileUploadEvent event) {
		if (this.getCurrent() != null) {
			if (this.getCurrent().getFile() == null)
				this.getCurrent().setFile(new File());
			File file = this.getCurrent().getFile();
			file.setFileName(event.getFile().getFileName());
			file.setFileContent(event.getFile().getContents());
			file.setFileContentType(event.getFile().getContentType());
			file.setFileSize(event.getFile().getSize());
		}
	}

	public StreamedContent getFile() throws IOException {
		if (this.getCurrent() == null || this.getCurrent().getFile() == null)
			return new DefaultStreamedContent();
		return bytesToImageConverter.getImage(this.getCurrent().getFile());
	}

	@Override
	public void create() throws Exception {
		this.getCurrent().setPublisherProfile(profileHelper.getCurrentProfile());
		super.create();
	}
}
