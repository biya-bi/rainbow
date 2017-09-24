package org.rainbow.asset.explorer.faces.converters;

import java.util.ResourceBundle;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.rainbow.asset.explorer.faces.utilities.ResourceBundles;
import org.rainbow.asset.explorer.orm.entities.PhoneType;

/**
 *
 * @author Biya-Bi
 */
@FacesConverter("org.rainbow.asset.explorer.faces.converters.PhoneTypeConverter")
public class PhoneTypeConverter implements Converter {

    private final ResourceBundle resourceBundle;

    public PhoneTypeConverter() {
        resourceBundle = ResourceBundle.getBundle(ResourceBundles.LABELS);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        PhoneType phoneType = (PhoneType) value;
        switch (phoneType) {
            case BUSINESS:
                return resourceBundle.getString("Business");
            case HOME:
                return resourceBundle.getString("Home");
            case MOBILE:
                return resourceBundle.getString("Mobile");
            default:
                return resourceBundle.getString("Other");
        }
    }
}
