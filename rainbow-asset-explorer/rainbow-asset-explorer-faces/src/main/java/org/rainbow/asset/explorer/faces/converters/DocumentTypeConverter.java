package org.rainbow.asset.explorer.faces.converters;

import java.util.ResourceBundle;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.rainbow.asset.explorer.faces.util.ResourceBundles;
import org.rainbow.asset.explorer.orm.entities.DocumentType;

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
        throw new UnsupportedOperationException("Not supported yet."); 
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
