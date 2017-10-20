package org.rainbow.asset.explorer.faces.controllers.write;

import java.io.ByteArrayInputStream;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.rainbow.asset.explorer.faces.util.ResourceBundles;
import org.rainbow.asset.explorer.orm.entities.AssetDocument;
import org.rainbow.asset.explorer.service.services.AssetDocumentService;
import org.rainbow.faces.controllers.write.AbstractWriteController;
import org.rainbow.faces.util.CrudNotificationInfo;
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
@CrudNotificationInfo(baseName = ResourceBundles.CRUD_MESSAGES, createdMessageKey = "AssetDocumentCreated", updatedMessageKey = "AssetDocumentUpdated", deletedMessageKey = "AssetDocumentDeleted")
public class AssetDocumentWriteController extends AbstractWriteController<AssetDocument> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 874602380591976731L;

	@Autowired
	private AssetDocumentService service;

	public AssetDocumentWriteController() {
		super(AssetDocument.class);
	}

	public void upload(FileUploadEvent event) throws Exception {
		UploadedFile file = event.getFile();

		AssetDocument current = this.getModel();
		current.setFileName(file.getFileName());
		current.setFileContent(file.getContents());
		current.setFileContentType(file.getContentType());
		current.setFileSize(file.getSize());
	}

	public StreamedContent getFile() {
		AssetDocument current = this.getModel();
		return new DefaultStreamedContent(new ByteArrayInputStream(current.getFileContent()),
				current.getFileContentType(), current.getFileName());
	}

	@Override
	public Service<AssetDocument> getService() {
		return service;
	}
}
