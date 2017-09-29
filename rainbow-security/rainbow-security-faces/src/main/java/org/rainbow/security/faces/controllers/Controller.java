package org.rainbow.security.faces.controllers;

import static org.rainbow.security.faces.utilities.ResourceBundles.CRUD_MESSAGES;
import static org.rainbow.security.faces.utilities.ResourceBundles.MESSAGES;
import static org.rainbow.security.faces.utilities.ResourceBundles.SECURITY_MESSAGES;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.primefaces.context.RequestContext;
import org.rainbow.faces.utilities.FacesContextUtil;
import org.rainbow.orm.entities.Trackable;
import org.rainbow.persistence.exceptions.NonexistentEntityException;
import org.rainbow.security.faces.utilities.CrudNotificationInfo;
import org.rainbow.service.services.Service;

/**
 *
 * @author Biya-Bi
 * @param <TEntity>
 */
public abstract class Controller<TEntity extends Trackable<TKey>, TKey extends Serializable, TSearchOptions>
		implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2668614258570663861L;
	private TEntity current;
	private Class<TEntity> modelClass;

	private CrudNotificationInfo crudNotificationInfo;

	private static final String DEFAULT_CREATED_MEASSAGE = "Object created successfully.";
	private static final String DEFAULT_UPDATED_MEASSAGE = "Object updated successfully.";
	private static final String DEFAULT_DELETED_MEASSAGE = "Object deleted successfully.";
	private static final String UNEXPECTED_ERROR_KEY = "UnexpectedErrorOccured";
	private static final String CRUD_NON_EXISTENT_ENTITY_ACCESSED = "NonexistentEntityAccessed";
	private static final String AUTHORIZATION_REFUSED = "AuthorizationRefused";

	protected static final String COMMITTED_FLAG = "committed";

	public Controller() {
	}

	protected abstract Service<TEntity, TKey, TSearchOptions> getService();

	public Controller(Class<TEntity> modelClass) {
		this.modelClass = modelClass;
	}

	public TEntity getCurrent() {
		return current;
	}

	public void setCurrent(TEntity current) {
		this.current = current;
	}

	public void create() throws Exception {
		try {
			getService().create(current);
			this.current = null;
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
			getService().update(current);
			this.current = null;
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
			getService().delete(current);
			this.current = null;
			FacesContextUtil.addSuccessMessage(getCudMessage(Operation.DELETE));
			RequestContext.getCurrentInstance().addCallbackParam(COMMITTED_FLAG, true);
		} catch (Throwable e) {
			RequestContext.getCurrentInstance().addCallbackParam(COMMITTED_FLAG, false);
			if (!handle(e)) {
				throw e;
			}
		}
	}

	public TEntity prepareCreate() {
		try {
			return this.current = modelClass.getConstructor().newInstance(new Object[] {});
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
			FacesContextUtil
					.addErrorMessage(ResourceBundle.getBundle(SECURITY_MESSAGES).getString(AUTHORIZATION_REFUSED));
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
