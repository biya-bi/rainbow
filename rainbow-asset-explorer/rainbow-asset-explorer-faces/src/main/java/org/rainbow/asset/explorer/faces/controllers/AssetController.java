/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.faces.controllers;

import static org.rainbow.asset.explorer.faces.utilities.ResourceBundles.CRUD_MESSAGES;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.rainbow.asset.explorer.core.entities.Asset;
import org.rainbow.asset.explorer.core.entities.Product;
import org.rainbow.asset.explorer.core.persistence.exceptions.DuplicateAssetSerialNumberException;
import org.rainbow.asset.explorer.core.service.ProductService;
import org.rainbow.asset.explorer.faces.exceptions.ProductNotFoundByNumberExpception;
import org.rainbow.asset.explorer.faces.models.SessionBean;
import org.rainbow.asset.explorer.faces.utilities.CrudNotificationInfo;
import org.rainbow.asset.explorer.faces.utilities.JsfUtil;
import org.rainbow.asset.explorer.faces.utilities.ResourceBundles;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
@CrudNotificationInfo(createdMessageKey = "AssetCreated", updatedMessageKey = "AssetUpdated", deletedMessageKey = "AssetDeleted")
public class AssetController extends TrackableController<Asset, Long, SearchOptions> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8492560344114250456L;

	private static final String DUPLICATE_ASSET_SERIAL_NUMBER_ERROR_KEY = "DuplicateAssetSerialNumber";
	private static final String ASSETS_IMPORTED_SUCCESSFULLY_ERROR_KEY = "AssetsImportedSuccessfully";
	private static final String NO_ASSETS_TO_IMPORT_FOUND_ERROR_KEY = "NoAssetsToImportFound";
	private static final String PRODUCT_NOT_FOUND_BY_NUMBER_ERROR_KEY = "ProductNotFoundByNumber";
	private static final String ASSET_NODE_NAME = "asset";
	private static final String SERIAL_NUMBER_NODE_NAME = "serialNumber";
	private static final String NAME_NODE_NAME = "name";
	private static final String PRODUCT_NUMBER_NODE_NAME = "productNumber";

	@Autowired
	@Qualifier("assetService")
	private Service<Asset, Long, SearchOptions> service;

	@Autowired
	@Qualifier("productService")
	private ProductService productService;

	public AssetController() {
		super(Asset.class);
	}

	@Override
	protected boolean handle(Throwable throwable) {
		if (throwable instanceof DuplicateAssetSerialNumberException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			DuplicateAssetSerialNumberException e = (DuplicateAssetSerialNumberException) throwable;
			JsfUtil.addErrorMessage(String.format(
					ResourceBundle.getBundle(CRUD_MESSAGES).getString(DUPLICATE_ASSET_SERIAL_NUMBER_ERROR_KEY),
					e.getSerialNumber()));
			return true;
		} else if (throwable instanceof ProductNotFoundByNumberExpception) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			ProductNotFoundByNumberExpception e = (ProductNotFoundByNumberExpception) throwable;
			JsfUtil.addErrorMessage(String.format(
					ResourceBundle.getBundle(CRUD_MESSAGES).getString(PRODUCT_NOT_FOUND_BY_NUMBER_ERROR_KEY),
					e.getProductNumber()));
			return true;
		}
		return super.handle(throwable);
	}

	private List<Asset> getAssets(Document doc) throws Exception {
		List<Asset> assets = new ArrayList<>();

		if (!doc.hasChildNodes()) {
			return assets; // TODO: Throw an exception here
		}

		NodeList nodeList = doc.getDocumentElement().getChildNodes();

		String username = (String) SessionBean.getSession().getAttribute("username");

		Map<String, Product> productsByNumbers = new HashMap<>();

		for (int count = 0; count < nodeList.getLength(); count++) {

			Node assetNode = nodeList.item(count);
			if (assetNode.getNodeType() == Node.ELEMENT_NODE) {
				if (ASSET_NODE_NAME.toUpperCase().equals(assetNode.getNodeName().toUpperCase())) {
					Asset asset = prepareCreate();
					asset.setCreator(username);
					asset.setUpdater(username);

					if (assetNode.hasChildNodes()) {
						NodeList assetNodeChildNodes = assetNode.getChildNodes();
						for (int i = 0; i < assetNodeChildNodes.getLength(); i++) {
							Node assetNodeChildNode = assetNodeChildNodes.item(i);
							if (assetNodeChildNode.getNodeType() == Node.ELEMENT_NODE
									&& assetNodeChildNode.hasChildNodes()) {
								NodeList children = assetNodeChildNode.getChildNodes();
								String valueNode = children.item(0).getNodeValue();
								if (SERIAL_NUMBER_NODE_NAME.toUpperCase()
										.equals(assetNodeChildNode.getNodeName().toUpperCase())) {
									asset.setSerialNumber(valueNode);
								} else if (NAME_NODE_NAME.toUpperCase()
										.equals(assetNodeChildNode.getNodeName().toUpperCase())) {
									asset.setName(valueNode);
								} else if (PRODUCT_NUMBER_NODE_NAME.toUpperCase()
										.equals(assetNodeChildNode.getNodeName().toUpperCase())) {
									if (!productsByNumbers.containsKey(valueNode)) {
										Product product = new Product();
										product.setNumber(valueNode);
										productsByNumbers.put(valueNode, product);
										asset.setProduct(product);
									} else {
										asset.setProduct(productsByNumbers.get(valueNode));
									}
								}
							}
						}
					}

					assets.add(asset);
				}
			}
		}

		List<Product> products = productService.find(new ArrayList<>(productsByNumbers.keySet()));

		for (Asset asset : assets) {
			boolean productFound = false;
			for (Product product : products) {
				if (asset.getProduct().getNumber().toUpperCase().equals(product.getNumber().toUpperCase())) {
					asset.setProduct(product);
					productFound = true;
					break;
				}
			}
			if (!productFound) {
				throw new ProductNotFoundByNumberExpception(asset.getProduct().getNumber());
			}
		}
		return assets;
	}

	public void bulkCreate(FileUploadEvent event)
			throws SAXException, ParserConfigurationException, IOException, Exception {
		try {
			UploadedFile file = event.getFile();
			DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

			Document doc = dBuilder.parse(file.getInputstream());

			List<Asset> assets = getAssets(doc);
			if (assets.isEmpty()) {
				JsfUtil.addErrorMessage(ResourceBundle.getBundle(ResourceBundles.VALIDATION_MESSAGES)
						.getString(NO_ASSETS_TO_IMPORT_FOUND_ERROR_KEY));
			} else {
				service.create(assets);
				JsfUtil.addSuccessMessage(
						(ResourceBundle.getBundle(CRUD_MESSAGES).getString(ASSETS_IMPORTED_SUCCESSFULLY_ERROR_KEY)));
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
	protected Service<Asset, Long, SearchOptions> getService() {
		return service;
	}
}
