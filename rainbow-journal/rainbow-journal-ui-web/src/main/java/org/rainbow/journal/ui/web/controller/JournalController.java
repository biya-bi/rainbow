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
import org.rainbow.journal.core.entities.Journal;
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
@CrudNotificationInfo(createdMessageKey = "JournalCreated", updatedMessageKey = "JournalUpdated", deletedMessageKey = "JournalDeleted")
public class JournalController extends TrackableController<Journal, Long, SearchOptions> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6507293478893921290L;

	@Autowired
	@Qualifier("journalService")
	private Service<Journal, Long, SearchOptions> service;

	@Autowired
	private ProfileHelper profileHelper;

	@Autowired
	private BytesToImageConverter bytesToImageConverter;

	public JournalController() {
		super(Journal.class);
	}

	@Override
	protected Service<Journal, Long, SearchOptions> getService() {
		return service;
	}

	public void uploadPhoto(FileUploadEvent event) {
		if (this.getCurrent() != null) {
			if (this.getCurrent().getPhoto() == null)
				this.getCurrent().setPhoto(new File());
			File file = this.getCurrent().getPhoto();
			file.setFileName(event.getFile().getFileName());
			file.setFileContent(event.getFile().getContents());
			file.setFileContentType(event.getFile().getContentType());
			file.setFileSize(event.getFile().getSize());
		}
	}

	public StreamedContent getPhoto() throws IOException {
		if (this.getCurrent() == null || this.getCurrent().getPhoto() == null)
			return new DefaultStreamedContent();
		return bytesToImageConverter.getImage(this.getCurrent().getPhoto());
	}

	@Override
	public void create() throws Exception {

		this.getCurrent().setOwnerProfile(profileHelper.getCurrentProfile());

		super.create();
	}
}
