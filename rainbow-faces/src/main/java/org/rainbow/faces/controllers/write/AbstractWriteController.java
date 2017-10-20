package org.rainbow.faces.controllers.write;

import static org.rainbow.faces.controllers.write.ResourceBundles.CRUD_MESSAGES;
import static org.rainbow.faces.controllers.write.ResourceBundles.MESSAGES;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.primefaces.context.RequestContext;
import org.rainbow.faces.controllers.ServiceController;
import org.rainbow.faces.util.CrudNotificationInfo;
import org.rainbow.faces.util.FacesContextUtil;
import org.rainbow.persistence.exceptions.NonexistentEntityException;

/**
 *
 * @author Biya-Bi
 * @param <T>
 */
public abstract class AbstractWriteController<T> implements Serializable, ServiceController<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7203175666695448619L;

	private T model;
	private Class<T> modelClass;

	private CrudNotificationInfo crudNotificationInfo;

	private static final String DEFAULT_CREATED_MEASSAGE = "Object created successfully.";
	private static final String DEFAULT_UPDATED_MEASSAGE = "Object updated successfully.";
	private static final String DEFAULT_DELETED_MEASSAGE = "Object deleted successfully.";
	private static final String UNEXPECTED_ERROR_KEY = "UnexpectedErrorOccured";
	private static final String CRUD_NON_EXISTENT_ENTITY_ACCESSED = "NonexistentEntityAccessed";
	private static final String AUTHORIZATION_REFUSED = "AuthorizationRefused";

	protected static final String COMMITTED_FLAG = "committed";

	public AbstractWriteController() {
	}

	public AbstractWriteController(Class<T> modelClass) {
		this.modelClass = Objects.requireNonNull(modelClass);
	}

	public T getModel() {
		return model;
	}

	public void setModel(T model) {
		this.model = model;
	}

	public void create() throws Exception {
		try {
			getService().create(model);
			this.model = null;
			FacesContextUtil.addSuccessMessage(getCudMessage(Operation.CREATE));
			RequestContext.getCurrentInstance().addCallbackParam(COMMITTED_FLAG, true);
		} catch (Throwable e) {
			RequestContext.getCurrentInstance().addCallbackParam(COMMITTED_FLAG, false);
			if (!handle(e)) {
				throw e;
			}
		}
	}

	public void edit() throws Exception {
		try {
			getService().update(model);
			this.model = null;
			FacesContextUtil.addSuccessMessage(getCudMessage(Operation.UPDATE));
			RequestContext.getCurrentInstance().addCallbackParam(COMMITTED_FLAG, true);
		} catch (Throwable e) {
			RequestContext.getCurrentInstance().addCallbackParam(COMMITTED_FLAG, false);
			if (!handle(e)) {
				throw e;
			}
		}
	}

	public void delete() throws Exception {
		try {
			getService().delete(model);
			this.model = null;
			FacesContextUtil.addSuccessMessage(getCudMessage(Operation.DELETE));
			RequestContext.getCurrentInstance().addCallbackParam(COMMITTED_FLAG, true);
		} catch (Throwable e) {
			RequestContext.getCurrentInstance().addCallbackParam(COMMITTED_FLAG, false);
			if (!handle(e)) {
				throw e;
			}
		}
	}

	public T prepareCreate() {
		if (this.modelClass == null) {
			throw new IllegalStateException("The model class can not be null.");
		}
		try {
			return this.model = modelClass.getConstructor().newInstance(new Object[] {});
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException ex) {
			throw new RuntimeException(ex);
		}
	}

	private CrudNotificationInfo getCrudNotificationInfo() {
		if (this.crudNotificationInfo == null) {
			CrudNotificationInfo[] annotations = this.getClass().getAnnotationsByType(CrudNotificationInfo.class);
			if (annotations.length > 0) {
				crudNotificationInfo = annotations[0];
			}
		}
		return crudNotificationInfo;
	}

	private enum Operation {
		CREATE, UPDATE, DELETE
	}

	private String getCudMessage(Operation operation) {
		CrudNotificationInfo info = getCrudNotificationInfo();
		if (info != null) {
			String messageKey;
			switch (operation) {
			case CREATE:
				messageKey = info.createdMessageKey();
				break;
			case UPDATE:
				messageKey = info.updatedMessageKey();
				break;
			default:
				messageKey = info.deletedMessageKey();
			}
			if (info.baseName() != null && !info.baseName().trim().isEmpty()) {
				return ResourceBundle.getBundle(info.baseName()).getString(messageKey);
			}
		}
		switch (operation) {
		case CREATE:
			return DEFAULT_CREATED_MEASSAGE;
		case UPDATE:
			return DEFAULT_UPDATED_MEASSAGE;
		default:
			return DEFAULT_DELETED_MEASSAGE;
		}
	}

	protected boolean handle(Throwable throwable) {
		Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
		NonexistentEntityException exception = extractException(throwable, NonexistentEntityException.class);
		if (exception != null) {
			FacesContextUtil.addErrorMessage(
					ResourceBundle.getBundle(CRUD_MESSAGES).getString(CRUD_NON_EXISTENT_ENTITY_ACCESSED));
			return true;
		}
		SecurityException se = extractException(throwable, SecurityException.class);
		if (se != null) {
			FacesContextUtil.addErrorMessage(ResourceBundle.getBundle(MESSAGES).getString(AUTHORIZATION_REFUSED));
			return true;
		}
		FacesContextUtil.addErrorMessage(ResourceBundle.getBundle(MESSAGES).getString(UNEXPECTED_ERROR_KEY));
		return true;
	}

	@SuppressWarnings("unchecked")
	private <U extends Throwable> U extractException(Throwable e, Class<U> exceptionClass) {
		Throwable t = e;
		if (!t.getClass().equals(exceptionClass)) {
			while (t != null && !t.getClass().equals(exceptionClass)) {
				t = t.getCause();
			}
		}
		if (t != null && t.getClass().equals(exceptionClass)) {
			return (U) t;
		}
		return null;
	}
}
