package org.rainbow.asset.explorer.faces.converters;

import java.util.ResourceBundle;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.rainbow.asset.explorer.faces.util.ResourceBundles;
import org.rainbow.asset.explorer.orm.entities.EmailType;

/**
 *
 * @author Biya-Bi
 */
@FacesConverter("org.rainbow.asset.explorer.faces.converters.EmailTypeConverter")
public class EmailTypeConverter implements Converter {

    private final ResourceBundle resourceBundle;

    public EmailTypeConverter() {
        resourceBundle = ResourceBundle.getBundle(ResourceBundles.LABELS);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        EmailType emailType = (EmailType) value;
        switch (emailType) {
            case EMAIL1:
                return resourceBundle.getString("Email1");
            case EMAIL2:
                return resourceBundle.getString("Email2");
            default:
                return resourceBundle.getString("Email3");
        }
    }
}
