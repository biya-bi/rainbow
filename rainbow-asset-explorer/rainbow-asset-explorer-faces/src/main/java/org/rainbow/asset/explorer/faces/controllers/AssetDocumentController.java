package org.rainbow.asset.explorer.faces.controllers;

import java.io.ByteArrayInputStream;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.rainbow.asset.explorer.faces.utilities.CrudNotificationInfo;
import org.rainbow.asset.explorer.orm.entities.AssetDocument;
import org.rainbow.persistence.SearchOptions;
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
@CrudNotificationInfo(createdMessageKey = "AssetDocumentCreated", updatedMessageKey = "AssetDocumentUpdated", deletedMessageKey = "AssetDocumentDeleted")
public class AssetDocumentController extends AuditableController<AssetDocument, Long, SearchOptions> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 874602380591976731L;


	@Autowired
	@Qualifier("assetDocumentService")
	private Service<AssetDocument, Long, SearchOptions> service;

    public AssetDocumentController() {
        super(AssetDocument.class);
    }

    public void upload(FileUploadEvent event) throws Exception {
        UploadedFile file = event.getFile();

        AssetDocument current = this.getCurrent();
        current.setFileName(file.getFileName());
        current.setFileContent(file.getContents());
        current.setFileContentType(file.getContentType());
        current.setFileSize(file.getSize());
    }

    public StreamedContent getFile() {
        AssetDocument current = this.getCurrent();
        return new DefaultStreamedContent(new ByteArrayInputStream(current.getFileContent()), current.getFileContentType(), current.getFileName());
    }

	@Override
	protected Service<AssetDocument, Long, SearchOptions> getService() {
		return service;
	}
}
