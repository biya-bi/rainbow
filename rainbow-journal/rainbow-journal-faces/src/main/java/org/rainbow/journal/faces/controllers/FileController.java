package org.rainbow.journal.faces.controllers;

import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.inject.Named;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.rainbow.journal.faces.util.BytesToImageConverter;
import org.rainbow.journal.orm.entities.File;
import org.rainbow.journal.orm.entities.Journal;
import org.rainbow.journal.orm.entities.Publication;
import org.rainbow.journal.service.services.JournalService;
import org.rainbow.journal.service.services.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Named
@RequestScoped
public class FileController {

	@Autowired
	@Qualifier("journalService")
	private JournalService journalService;

	@Autowired
	@Qualifier("publicationService")
	private PublicationService publicationService;

	@Autowired
	private BytesToImageConverter bytesToImageConverter;

	private static final String JOURNAL_ID_PARAM_NAME = "journalId";
	private static final String PUBLICATION_ID_PARAM_NAME = "publicationId";

	private StreamedContent getPhoto(String paramName) throws NumberFormatException, Exception {
		if (FacesContext.getCurrentInstance().getCurrentPhaseId() == PhaseId.RENDER_RESPONSE)
			return new DefaultStreamedContent();

		File file = null;
		String id = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get(paramName);
		if (paramName == JOURNAL_ID_PARAM_NAME) {
			Journal journal = journalService.findById(Long.valueOf(id));
			if (journal != null && journal.getPhoto() != null)
				file = journal.getPhoto();
		} else if (paramName == PUBLICATION_ID_PARAM_NAME) {
			Publication publication = publicationService.findById(Long.valueOf(id));
			if (publication != null && publication.getFile() != null)
				file = publication.getFile();
		}
		if (file != null)
			return bytesToImageConverter.getImage(file);
		return new DefaultStreamedContent();
	}

	public StreamedContent getJournalPhoto() throws NumberFormatException, Exception {
		return getPhoto(JOURNAL_ID_PARAM_NAME);
	}

	public StreamedContent getPublicationFile() throws NumberFormatException, Exception {
		return getPhoto(PUBLICATION_ID_PARAM_NAME);
	}
}
