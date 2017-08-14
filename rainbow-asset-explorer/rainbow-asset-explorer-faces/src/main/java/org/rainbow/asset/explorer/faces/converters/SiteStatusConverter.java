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

import org.rainbow.asset.explorer.core.entities.SiteStatus;
import org.rainbow.asset.explorer.faces.utilities.ResourceBundles;

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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
