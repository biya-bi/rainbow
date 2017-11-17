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
import org.rainbow.journal.orm.entities.Publication;
import org.rainbow.journal.service.services.PublicationService;
import org.rainbow.service.services.Service;
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
@CrudNotificationInfo(baseName = ResourceBundles.CRUD_MESSAGES, createdMessageKey = "PublicationCreated", updatedMessageKey = "PublicationUpdated", deletedMessageKey = "PublicationDeleted")
public class PublicationWriteController extends AbstractWriteController<Publication> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8860697631924881525L;

	@Autowired
	@Qualifier("publicationService")
	private PublicationService service;

	@Autowired
	private BytesToImageConverter bytesToImageConverter;

	@Autowired
	private ProfileHelper profileHelper;

	public PublicationWriteController() {
		super(Publication.class);
	}

	@Override
	public Service<Publication> getService() {
		return service;
	}

	public void uploadFile(FileUploadEvent event) {
		if (this.getModel() != null) {
			if (this.getModel().getFile() == null)
				this.getModel().setFile(new File());
			File file = this.getModel().getFile();
			file.setFileName(event.getFile().getFileName());
			file.setFileContent(event.getFile().getContents());
			file.setFileContentType(event.getFile().getContentType());
			file.setFileSize(event.getFile().getSize());
		}
	}

	public StreamedContent getFile() throws IOException {
		if (this.getModel() == null || this.getModel().getFile() == null)
			return new DefaultStreamedContent();
		return bytesToImageConverter.getImage(this.getModel().getFile());
	}

	@Override
	public void create() throws Exception {
		this.getModel().setPublisherProfile(profileHelper.getCurrentProfile());
		super.create();
	}
}
