/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.faces.converters;

import java.util.ResourceBundle;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.rainbow.asset.explorer.core.entities.DocumentType;
import org.rainbow.asset.explorer.faces.utilities.ResourceBundles;

/**
 *
 * @author Biya-Bi
 */
@FacesConverter("org.rainbow.asset.explorer.faces.converters.DocumentTypeConverter")
public class DocumentTypeConverter implements Converter {

    private final ResourceBundle resourceBundle;

    public DocumentTypeConverter() {
        resourceBundle = ResourceBundle.getBundle(ResourceBundles.LABELS);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        DocumentType documentType = (DocumentType) value;
        switch (documentType) {
            case FINANCIAL_DOCUMENT:
                return resourceBundle.getString("DocumentTypeFinancial");
            case LEGAL_DOCUMENT:
                return resourceBundle.getString("DocumentTypeLegal");
            case WARRANTY:
                return resourceBundle.getString("DocumentTypeWarranty");
            default:
                return null;
        }
    }
}
