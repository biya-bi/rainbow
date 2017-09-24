package org.rainbow.asset.explorer.faces.converters;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.rainbow.asset.explorer.faces.translation.EnumTranslator;
import org.rainbow.asset.explorer.orm.entities.PurchaseOrderStatus;

/**
 *
 * @author Biya-Bi
 */
@FacesConverter("org.rainbow.asset.explorer.faces.converters.PurchaseOrderStatusConverter")
public class PurchaseOrderStatusConverter implements Converter {

    private final EnumTranslator translator;

    public PurchaseOrderStatusConverter() {
        translator = new EnumTranslator();
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null || value.toString().trim().isEmpty()) {
            return "";
        }
        return translator.translate((PurchaseOrderStatus) value);
    }
}
