package org.rainbow.asset.explorer.faces.converters;

import java.util.ResourceBundle;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.rainbow.asset.explorer.faces.util.ResourceBundles;
import org.rainbow.asset.explorer.orm.entities.FaxType;

/**
 *
 * @author Biya-Bi
 */
@FacesConverter("org.rainbow.asset.explorer.faces.converters.FaxTypeConverter")
public class FaxTypeConverter implements Converter {

    private final ResourceBundle resourceBundle;

    public FaxTypeConverter() {
        resourceBundle = ResourceBundle.getBundle(ResourceBundles.LABELS);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        FaxType faxType = (FaxType) value;
        switch (faxType) {
            case BUSINESS:
                return resourceBundle.getString("Business");
            case HOME:
                return resourceBundle.getString("Home");
            default:
                return resourceBundle.getString("Other");
        }
    }
}
