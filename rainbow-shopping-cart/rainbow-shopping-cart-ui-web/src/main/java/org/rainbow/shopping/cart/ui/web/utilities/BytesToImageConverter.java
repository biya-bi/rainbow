package org.rainbow.shopping.cart.ui.web.utilities;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.rainbow.shopping.cart.model.Photo;
import org.springframework.stereotype.Component;

@Component
public class BytesToImageConverter {

	public StreamedContent getImage(Photo photo) throws IOException {
		if (photo == null || photo.getFileContent() == null || photo.getFileContentType() == null
				|| FacesContext.getCurrentInstance().getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
			return new DefaultStreamedContent();
		} else {
			return new DefaultStreamedContent(new ByteArrayInputStream(photo.getFileContent()),
					photo.getFileContentType());
		}
	}

}
