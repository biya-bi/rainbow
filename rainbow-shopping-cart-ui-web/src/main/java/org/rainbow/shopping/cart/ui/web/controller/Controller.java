/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.shopping.cart.ui.web.controller;

import static org.rainbow.shopping.cart.ui.web.utilities.ResourceBundles.CRUD_MESSAGES;
import static org.rainbow.shopping.cart.ui.web.utilities.ResourceBundles.MESSAGES;
import static org.rainbow.shopping.cart.ui.web.utilities.ResourceBundles.SECURITY_MESSAGES;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityNotFoundException;

import org.primefaces.context.RequestContext;
import org.rainbow.service.api.IService;
import org.rainbow.shopping.cart.ui.web.utilities.CrudNotificationInfo;
import org.rainbow.shopping.cart.ui.web.utilities.JsfUtil;

/**
 *
 * @author Biya-Bi
 * @param <T>
 */
public abstract class Controller<T> implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7203175666695448619L;

	private T current;
    private Class<?> modelClass;

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


    protected abstract IService<T> getService();

    public Controller(Class<?> modelClass) {
        this.modelClass = modelClass;
    }

    public T getCurrent() {
        return current;
    }

    public void setCurrent(T current) {
        this.current = current;
    }

    public void create() throws Exception {
        try {
            getService().create(current);
            this.current = null;
            JsfUtil.addSuccessMessage(getCudMessage(Operation.CREATE));
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
            JsfUtil.addSuccessMessage(getCudMessage(Operation.UPDATE));
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
            JsfUtil.addSuccessMessage(getCudMessage(Operation.DELETE));
            RequestContext.getCurrentInstance().addCallbackParam(COMMITTED_FLAG, true);
        } catch (Throwable e) {
            RequestContext.getCurrentInstance().addCallbackParam(COMMITTED_FLAG, false);
            if (!handle(e)) {
                throw e;
            }
        }
    }

    @SuppressWarnings("unchecked")
	public T prepareCreate() {
        try {
            return this.current = (T) modelClass.getConstructor().newInstance(new Object[]{});
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
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
        EntityNotFoundException enfe = extractException(throwable, EntityNotFoundException.class);
        if (enfe != null) {
            JsfUtil.addErrorMessage(ResourceBundle.getBundle(CRUD_MESSAGES).getString(CRUD_NON_EXISTENT_ENTITY_ACCESSED));
            return true;
        }
        SecurityException se = extractException(throwable, SecurityException.class);
        if (se != null) {
            JsfUtil.addErrorMessage(ResourceBundle.getBundle(SECURITY_MESSAGES).getString(AUTHORIZATION_REFUSED));
            return true;
        }
        JsfUtil.addErrorMessage( ResourceBundle.getBundle(MESSAGES).getString(UNEXPECTED_ERROR_KEY));
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
