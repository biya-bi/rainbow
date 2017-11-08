package org.rainbow.asset.explorer.faces.handlers;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;

/**
 *
 * @author Biya-Bi
 */
public class DefaultExceptionHandler extends ExceptionHandlerWrapper {

    private final ExceptionHandler wrapped;

    public DefaultExceptionHandler(ExceptionHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ExceptionHandler getWrapped() {
        return this.wrapped;
    }

    @Override
    public void handle() throws FacesException {
        final Logger logger = Logger.getLogger(this.getClass().getName());
		try {

            for (Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator(); i.hasNext();) {
                try {
                    ExceptionQueuedEvent event = i.next();
                    ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();

                    Throwable t = context.getException();

                    logger.log(Level.SEVERE, null, t);

                    FacesContext fc = FacesContext.getCurrentInstance();
                    ExternalContext externalContext = fc.getExternalContext();

                    if (t instanceof ViewExpiredException) {
                        ViewExpiredException vee = (ViewExpiredException) t;
                        Map<String, Object> requestMap = externalContext.getRequestMap();
                        NavigationHandler nav = fc.getApplication().getNavigationHandler();

                        // Push some stuff to the request scope for later use in the page
                        requestMap.put("currentViewId", vee.getViewId());
                        nav.handleNavigation(fc, null, "security/login");
                        fc.renderResponse();
                    }
                    else {
                        SecurityException securityException = extractException(t, SecurityException.class);
                        if (securityException != null) {
                            String s = externalContext.getRequestContextPath() + "/errors/unauthorized.xhtml";
                            fc.getExternalContext().redirect(s);
                        }
                        else {
                            throw t;
                        }
                    }
                } finally {
                    i.remove();
                }
            }
            // Let the parent handle all the remaining queued exception events.
            getWrapped().handle();
        } catch (Throwable t) {
            FacesContext fc = FacesContext.getCurrentInstance();

            logger.log(Level.SEVERE, null, t);
            String s = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/errors/unexpected_error.xhtml";
            try {
                fc.getExternalContext().redirect(s);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
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
