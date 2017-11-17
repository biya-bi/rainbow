package org.rainbow.journal.faces.controllers.write;

import java.io.IOException;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.rainbow.faces.controllers.write.AbstractWriteController;
import org.rainbow.faces.util.CrudNotificationInfo;
import org.rainbow.journal.faces.util.BytesToImageConverter;
import org.rainbow.journal.faces.util.ProfileHelper;
import org.rainbow.journal.faces.util.ResourceBundles;
import org.rainbow.journal.orm.entities.File;
import org.rainbow.journal.orm.entities.Journal;
import org.rainbow.journal.service.services.JournalService;
import org.rainbow.service.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Named
@ViewScoped
@CrudNotificationInfo(baseName = ResourceBundles.CRUD_MESSAGES, createdMessageKey = "JournalCreated", updatedMessageKey = "JournalUpdated", deletedMessageKey = "JournalDeleted")
public class JournalWriteController extends AbstractWriteController<Journal> {
	/**
	* 
	*/
	private static final long serialVersionUID = 6507293478893921290L;

	@Autowired
	@Qualifier("journalService")
	private JournalService service;

	@Autowired
	private ProfileHelper profileHelper;

	@Autowired
	private BytesToImageConverter bytesToImageConverter;

	public JournalWriteController() {
		super(Journal.class);
	}

	@Override
	public Service<Journal> getService() {
		return service;
	}

	public void uploadPhoto(FileUploadEvent event) {
		if (this.getModel() != null) {
			if (this.getModel().getPhoto() == null)
				this.getModel().setPhoto(new File());
			File file = this.getModel().getPhoto();
			file.setFileName(event.getFile().getFileName());
			file.setFileContent(event.getFile().getContents());
			file.setFileContentType(event.getFile().getContentType());
			file.setFileSize(event.getFile().getSize());
		}
	}

	public StreamedContent getPhoto() throws IOException {
		if (this.getModel() == null || this.getModel().getPhoto() == null)
			return new DefaultStreamedContent();
		return bytesToImageConverter.getImage(this.getModel().getPhoto());
	}

	@Override
	public void create() throws Exception {

		this.getModel().setOwnerProfile(profileHelper.getCurrentProfile());

		super.create();
	}

}
