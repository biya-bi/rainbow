package org.rainbow.asset.explorer.faces.converters;

import java.util.ResourceBundle;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.rainbow.asset.explorer.faces.util.ResourceBundles;
import org.rainbow.asset.explorer.orm.entities.AlertCategory;

/**
 *
 * @author Biya-Bi
 */
@FacesConverter("org.rainbow.asset.explorer.faces.converters.AlertCategoryConverter")
public class AlertCategoryConverter implements Converter {

    private final ResourceBundle resourceBundle;

    public AlertCategoryConverter() {
        resourceBundle = ResourceBundle.getBundle(ResourceBundles.LABELS);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        AlertCategory alertCategory = (AlertCategory) value;
        switch (alertCategory) {
            case STOCK_LEVEL:
                return resourceBundle.getString("StockLevel");
            default:
                return null;
        }
    }
}
