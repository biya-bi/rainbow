package org.rainbow.asset.explorer.faces.converters;

import java.util.ResourceBundle;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.rainbow.asset.explorer.faces.utilities.ResourceBundles;
import org.rainbow.asset.explorer.orm.entities.AddressType;

/**
 *
 * @author Biya-Bi
 */
@FacesConverter("org.rainbow.asset.explorer.faces.converters.AddressTypeConverter")
public class AddressTypeConverter implements Converter {

    private final ResourceBundle resourceBundle;

    public AddressTypeConverter() {
        resourceBundle = ResourceBundle.getBundle(ResourceBundles.LABELS);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        AddressType addressType = (AddressType) value;
        switch (addressType) {
            case BILLING:
                return resourceBundle.getString("Billing");
            case BUSINESS:
                return resourceBundle.getString("Business");
            case HOME:
                return resourceBundle.getString("Home");
            case SHIPPING:
                return resourceBundle.getString("Shipping");
            default:
                return resourceBundle.getString("Other");
        }
    }
}
