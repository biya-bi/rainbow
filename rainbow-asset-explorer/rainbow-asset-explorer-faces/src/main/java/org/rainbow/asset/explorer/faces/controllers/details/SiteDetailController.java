package org.rainbow.asset.explorer.faces.controllers.details;

import static org.rainbow.asset.explorer.faces.util.ResourceBundles.CRUD_MESSAGES;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.primefaces.context.RequestContext;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.rainbow.asset.explorer.faces.util.ResourceBundles;
import org.rainbow.asset.explorer.orm.entities.Site;
import org.rainbow.asset.explorer.orm.entities.SiteStatus;
import org.rainbow.asset.explorer.service.exceptions.DuplicateSiteNameException;
import org.rainbow.asset.explorer.service.services.SiteService;
import org.rainbow.common.util.DateTime;
import org.rainbow.faces.controllers.details.AbstractAuditableDetailController;
import org.rainbow.faces.util.CrudNotificationInfo;
import org.rainbow.faces.util.FacesContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@ViewScoped
@CrudNotificationInfo(baseName = ResourceBundles.CRUD_MESSAGES, createdMessageKey = "SiteCreated", updatedMessageKey = "SiteUpdated", deletedMessageKey = "SiteDeleted")
public class SiteDetailController extends AbstractAuditableDetailController<Site> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4458370406113484288L;

	private static final String DUPLICATE_SITE_NAME_ERROR_KEY = "DuplicateSiteName";

	private static final String SITES_IMPORTED_SUCCESSFULLY_ERROR_KEY = "SitesImportedSuccessfully";
	private static final String NO_SITES_TO_IMPORT_FOUND_ERROR_KEY = "NoSitesToImportFound";
	private static final String SITE_NODE_NAME = "site";
	private static final String ADDRESS_NODE_NAME = "address";
	private static final String NAME_NODE_NAME = "name";
	private static final String LOCATION_NODE_NAME = "location";
	private static final String STATUS_NODE_NAME = "status";
	private static final String DATE_OF_COMMISSIONING_NODE_NAME = "dateOfCommissioning";
	private static final String DATE_OF_DECOMMISSIONING_NODE_NAME = "dateOfDecommissioning";

	@Autowired
	private SiteService service;

	public SiteDetailController() {
		super(Site.class);
	}

	@Override
	protected boolean handle(Throwable throwable) {
		if (throwable instanceof DuplicateSiteNameException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			DuplicateSiteNameException e = (DuplicateSiteNameException) throwable;
			FacesContextUtil.addErrorMessage(String.format(
					ResourceBundle.getBundle(CRUD_MESSAGES).getString(DUPLICATE_SITE_NAME_ERROR_KEY), e.getName()));
			return true;
		}
		return super.handle(throwable);
	}

	private List<Site> getSites(Document doc) throws Exception {
		List<Site> sites = new ArrayList<>();

		if (!doc.hasChildNodes()) {
			return sites; // TODO: Throw an exception here
		}

		NodeList nodeList = doc.getDocumentElement().getChildNodes();

		for (int count = 0; count < nodeList.getLength(); count++) {

			Node siteNode = nodeList.item(count);
			if (siteNode.getNodeType() == Node.ELEMENT_NODE) {
				if (SITE_NODE_NAME.toUpperCase().equals(siteNode.getNodeName().toUpperCase())) {
					Site site = prepareCreate();

					if (siteNode.hasChildNodes()) {
						NodeList siteNodeChildNodes = siteNode.getChildNodes();
						for (int i = 0; i < siteNodeChildNodes.getLength(); i++) {
							Node siteNodeChildNode = siteNodeChildNodes.item(i);
							if (siteNodeChildNode.getNodeType() == Node.ELEMENT_NODE
									&& siteNodeChildNode.hasChildNodes()) {
								NodeList children = siteNodeChildNode.getChildNodes();
								String valueNode = children.item(0).getNodeValue();
								if (ADDRESS_NODE_NAME.toUpperCase()
										.equals(siteNodeChildNode.getNodeName().toUpperCase())) {
									site.setAddress(valueNode);
								} else if (NAME_NODE_NAME.toUpperCase()
										.equals(siteNodeChildNode.getNodeName().toUpperCase())) {
									site.setName(valueNode);
								} else if (LOCATION_NODE_NAME.toUpperCase()
										.equals(siteNodeChildNode.getNodeName().toUpperCase())) {
									site.setLocation(valueNode);
								} else if (STATUS_NODE_NAME.toUpperCase()
										.equals(siteNodeChildNode.getNodeName().toUpperCase())) {
									site.setStatus(SiteStatus.valueOf(valueNode));
								} else if (DATE_OF_COMMISSIONING_NODE_NAME.toUpperCase()
										.equals(siteNodeChildNode.getNodeName().toUpperCase())) {
									if (valueNode != null && !valueNode.isEmpty()) {
										site.setDateOfCommissioning(DateTime.toDate(valueNode));
									}
								} else if (DATE_OF_DECOMMISSIONING_NODE_NAME.toUpperCase()
										.equals(siteNodeChildNode.getNodeName().toUpperCase())) {
									if (valueNode != null && !valueNode.isEmpty()) {
										site.setDateOfDecommissioning(DateTime.toDate(valueNode));
									}
								}
							}
						}
					}

					sites.add(site);
				}
			}
		}
		return sites;
	}

	public void bulkCreate(FileUploadEvent event)
			throws SAXException, ParserConfigurationException, IOException, Exception {
		try {
			UploadedFile file = event.getFile();
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			Document doc = dBuilder.parse(file.getInputstream());

			List<Site> sites = getSites(doc);
			if (sites.isEmpty()) {
				FacesContextUtil.addErrorMessage(ResourceBundle.getBundle(ResourceBundles.VALIDATION_MESSAGES)
						.getString(NO_SITES_TO_IMPORT_FOUND_ERROR_KEY));
			} else {
				service.create(sites);
				FacesContextUtil.addSuccessMessage(
						(ResourceBundle.getBundle(CRUD_MESSAGES).getString(SITES_IMPORTED_SUCCESSFULLY_ERROR_KEY)));
				RequestContext.getCurrentInstance().addCallbackParam(COMMITTED_FLAG, true);
			}
		} catch (Throwable throwable) {
			RequestContext.getCurrentInstance().addCallbackParam(COMMITTED_FLAG, false);
			if (!handle(throwable)) {
				throw throwable;
			}
		}
	}

	@Override
	protected SiteService getService() {
		return service;
	}
}
