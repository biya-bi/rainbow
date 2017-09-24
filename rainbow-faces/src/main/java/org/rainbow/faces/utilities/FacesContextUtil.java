package org.rainbow.faces.utilities;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

public class FacesContextUtil {

	public static boolean isValidationFailed() {
		return FacesContext.getCurrentInstance().isValidationFailed();
	}

	public static void addErrorMessage(Exception ex, String defaultMsg) {
		String msg = ex.getLocalizedMessage();
		if (msg != null && msg.length() > 0) {
			addErrorMessage(msg);
		} else {
			addErrorMessage(defaultMsg);
		}
	}

	public static void addMessage(Severity severity, String summary, String detail) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, detail));
	}

	public static void addMessage(Severity severity, String summary, String detail, String clientId) {
		FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(severity, summary, detail));
	}

	public static void addErrorMessages(List<String> messages) {
		for (String message : messages) {
			addErrorMessage(message);
		}
	}

	public static void addErrorMessage(String msg) {
		addMessage(FacesMessage.SEVERITY_ERROR, msg, msg);
	}

	public static void addErrorMessage(String summary, String detail) {
		addMessage(FacesMessage.SEVERITY_ERROR, summary, detail);
	}

	public static void addSuccessMessage(String msg) {
		addMessage(FacesMessage.SEVERITY_INFO, msg, msg);
	}

	public static String getRequestParameter(String key) {
		return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(key);
	}

	public static Object getObjectFromRequestParameter(String requestParameterName, Converter converter,
			UIComponent component) {
		String theId = FacesContextUtil.getRequestParameter(requestParameterName);
		return converter.getAsObject(FacesContext.getCurrentInstance(), component, theId);
	}
}
