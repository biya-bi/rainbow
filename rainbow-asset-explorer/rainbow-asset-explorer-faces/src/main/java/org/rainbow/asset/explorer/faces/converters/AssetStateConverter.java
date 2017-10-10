package org.rainbow.asset.explorer.faces.converters;

import java.util.ResourceBundle;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.rainbow.asset.explorer.faces.util.ResourceBundles;
import org.rainbow.asset.explorer.orm.entities.AssetState;

/**
 *
 * @author Biya-Bi
 */
@FacesConverter("org.rainbow.asset.explorer.faces.converters.AssetStateConverter")
public class AssetStateConverter implements Converter {

    private final ResourceBundle resourceBundle;

    public AssetStateConverter() {
        resourceBundle = ResourceBundle.getBundle(ResourceBundles.LABELS);
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        AssetState state = (AssetState) value;
        switch (state) {
            case DISPOSED:
                return resourceBundle.getString("AssetStateDisposed");
            case EXPIRED:
                return resourceBundle.getString("AssetStateExpired");
            case IN_REPAIR:
                return resourceBundle.getString("AssetStateInRepair");
            case IN_STORE:
                return resourceBundle.getString("AssetStateInStore");
            case IN_USE:
                return resourceBundle.getString("AssetStateInUse");
            default:
                return null;
        }
    }
}
