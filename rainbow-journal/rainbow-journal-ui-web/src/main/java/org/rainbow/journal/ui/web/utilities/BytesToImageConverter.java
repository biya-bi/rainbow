package org.rainbow.journal.ui.web.utilities;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.rainbow.journal.core.entities.File;
import org.springframework.stereotype.Component;

@Component
public class BytesToImageConverter {

	public StreamedContent getImage(File file) throws IOException {
		if (file == null || file.getFileContent() == null || file.getFileContentType() == null
				|| FacesContext.getCurrentInstance().getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
			return new DefaultStreamedContent();
		} else {
			return new DefaultStreamedContent(new ByteArrayInputStream(file.getFileContent()),
					file.getFileContentType());
		}
	}

}
