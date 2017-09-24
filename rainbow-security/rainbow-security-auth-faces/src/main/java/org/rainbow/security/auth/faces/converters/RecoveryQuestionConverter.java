package org.rainbow.security.auth.faces.converters;

import static org.rainbow.security.auth.faces.utilities.ResourceBundles.RECOVERY_QUESTIONS;

import java.util.ResourceBundle;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("org.rainbow.security.auth.faces.converters.RecoveryQuestionConverter")
public class RecoveryQuestionConverter implements Converter {

	private final ResourceBundle resourceBundle;

	public RecoveryQuestionConverter() {
		resourceBundle = ResourceBundle.getBundle(RECOVERY_QUESTIONS);
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value instanceof String)
			return resourceBundle.getString((String) value);
		return null;
	}
}
