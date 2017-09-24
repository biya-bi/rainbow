package org.rainbow.asset.explorer.faces.converters;

import java.util.ResourceBundle;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.rainbow.asset.explorer.faces.utilities.ResourceBundles;
import org.rainbow.asset.explorer.orm.entities.SiteStatus;

/**
 *
 * @author Biya-Bi
 */
@FacesConverter("org.rainbow.asset.explorer.faces.converters.SiteStatusConverter")
public class SiteStatusConverter implements Converter {

    private final ResourceBundle resourceBundle;

    public SiteStatusConverter() {
        resourceBundle = ResourceBundle.getBundle(ResourceBundles.LABELS);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        SiteStatus status = (SiteStatus) value;
        switch (status) {
            case NEW:
                return resourceBundle.getString("SiteStatusNew");
            case ACTIVE:
                return resourceBundle.getString("SiteStatusActive");
            case DECOMMISSIONED:
                return resourceBundle.getString("SiteStatusDecommissioned");
            default:
                return null;
        }
    }
}
