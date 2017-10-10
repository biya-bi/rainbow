package org.rainbow.asset.explorer.faces.converters;

import java.util.ResourceBundle;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.rainbow.asset.explorer.faces.util.ResourceBundles;
import org.rainbow.asset.explorer.orm.entities.AlertType;

/**
 *
 * @author Biya-Bi
 */
@FacesConverter("org.rainbow.asset.explorer.faces.converters.AlertTypeConverter")
public class AlertTypeConverter implements Converter {

    private final ResourceBundle resourceBundle;

    public AlertTypeConverter() {
        resourceBundle = ResourceBundle.getBundle(ResourceBundles.LABELS);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        AlertType alertType = (AlertType) value;
        switch (alertType) {
            case RECOVERY:
                return resourceBundle.getString("Recovery");
            case WARNING:
                return resourceBundle.getString("Warning");
            default:
                return null;
        }
    }
}
