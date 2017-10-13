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
import org.rainbow.asset.explorer.orm.entities.Product;
import org.rainbow.asset.explorer.service.exceptions.DuplicateProductNameException;
import org.rainbow.asset.explorer.service.exceptions.DuplicateProductNumberException;
import org.rainbow.asset.explorer.service.services.ProductService;
import org.rainbow.faces.controllers.details.AbstractAuditableDetailController;
import org.rainbow.faces.util.CrudNotificationInfo;
import org.rainbow.faces.util.FacesContextUtil;
import org.rainbow.service.services.Service;
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
@CrudNotificationInfo(baseName = ResourceBundles.CRUD_MESSAGES, createdMessageKey = "ProductCreated", updatedMessageKey = "ProductUpdated", deletedMessageKey = "ProductDeleted")
public class ProductDetailController extends AbstractAuditableDetailController<Product> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1423310559956400949L;

	private static final String DUPLICATE_PRODUCT_NUMBER_ERROR_KEY = "DuplicateProductNumber";
	private static final String DUPLICATE_PRODUCT_NAME_ERROR_KEY = "DuplicateProductName";
	private static final String PRODUCTS_IMPORTED_SUCCESSFULLY_ERROR_KEY = "ProductsImportedSuccessfully";
	private static final String NO_PRODUCTS_TO_IMPORT_FOUND_ERROR_KEY = "NoProductsToImportFound";
	private static final String PRODUCT_NODE_NAME = "item";
	private static final String NUMBER_NODE_NAME = "number";
	private static final String NAME_NODE_NAME = "name";
	private static final String REORDER_POINT_NODE_NAME = "reorder_point";

	@Autowired
	private ProductService service;

	public ProductDetailController() {
		super(Product.class);
	}

	@Override
	protected boolean handle(Throwable throwable) {
		if (throwable instanceof DuplicateProductNumberException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			DuplicateProductNumberException e = (DuplicateProductNumberException) throwable;
			FacesContextUtil.addErrorMessage(
					String.format(ResourceBundle.getBundle(CRUD_MESSAGES).getString(DUPLICATE_PRODUCT_NUMBER_ERROR_KEY),
							e.getNumber()));
			return true;
		} else if (throwable instanceof DuplicateProductNameException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			DuplicateProductNameException e = (DuplicateProductNameException) throwable;
			FacesContextUtil.addErrorMessage(String.format(
					ResourceBundle.getBundle(CRUD_MESSAGES).getString(DUPLICATE_PRODUCT_NAME_ERROR_KEY), e.getName()));
			return true;
		}
		return super.handle(throwable);
	}

	private List<Product> getProducts(Document doc) throws Exception {
		List<Product> products = new ArrayList<>();

		if (!doc.hasChildNodes()) {
			return products; // TODO: Throw an exception here
		}

		NodeList nodeList = doc.getDocumentElement().getChildNodes();

		for (int count = 0; count < nodeList.getLength(); count++) {

			Node productNode = nodeList.item(count);
			if (productNode.getNodeType() == Node.ELEMENT_NODE) {
				if (PRODUCT_NODE_NAME.toUpperCase().equals(productNode.getNodeName().toUpperCase())) {
					Product product = prepareCreate();

					if (productNode.hasChildNodes()) {
						NodeList productNodeChildNodes = productNode.getChildNodes();
						for (int i = 0; i < productNodeChildNodes.getLength(); i++) {
							Node productNodeChildNode = productNodeChildNodes.item(i);
							if (productNodeChildNode.getNodeType() == Node.ELEMENT_NODE
									&& productNodeChildNode.hasChildNodes()) {
								NodeList children = productNodeChildNode.getChildNodes();
								String valueNode = children.item(0).getNodeValue();
								if (NUMBER_NODE_NAME.toUpperCase()
										.equals(productNodeChildNode.getNodeName().toUpperCase())) {
									product.setNumber(valueNode);
								} else if (NAME_NODE_NAME.toUpperCase()
										.equals(productNodeChildNode.getNodeName().toUpperCase())) {
									product.setName(valueNode);
								} else if (REORDER_POINT_NODE_NAME.toUpperCase()
										.equals(productNodeChildNode.getNodeName().toUpperCase())) {
									product.setReorderPoint(Short.valueOf(valueNode));
								}
							}
						}
					}

					products.add(product);
				}
			}
		}
		return products;
	}

	public void bulkCreate(FileUploadEvent event)
			throws SAXException, ParserConfigurationException, IOException, Exception {
		try {
			UploadedFile file = event.getFile();
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			Document doc = dBuilder.parse(file.getInputstream());

			List<Product> products = getProducts(doc);
			if (products.isEmpty()) {
				FacesContextUtil.addErrorMessage(ResourceBundle.getBundle(ResourceBundles.VALIDATION_MESSAGES)
						.getString(NO_PRODUCTS_TO_IMPORT_FOUND_ERROR_KEY));
			} else {
				service.create(products);
				FacesContextUtil.addSuccessMessage(
						(ResourceBundle.getBundle(CRUD_MESSAGES).getString(PRODUCTS_IMPORTED_SUCCESSFULLY_ERROR_KEY)));
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
	protected Service<Product> getService() {
		return service;
	}
}
