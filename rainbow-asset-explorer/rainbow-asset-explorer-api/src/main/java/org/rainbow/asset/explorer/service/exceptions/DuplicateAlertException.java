package org.rainbow.asset.explorer.service.exceptions;

import org.rainbow.asset.explorer.orm.entities.AlertCategory;
import org.rainbow.asset.explorer.orm.entities.AlertType;
import org.rainbow.asset.explorer.service.exceptions.RainbowAssetExplorerException;

/**
 *
 * @author Biya-Bi
 */
public class DuplicateAlertException extends RainbowAssetExplorerException {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7570972760369858653L;
	private final AlertType alertType;
    private final AlertCategory alertCategory;

    public DuplicateAlertException(AlertType alertType, AlertCategory alertCategory) {
        this(alertType, alertCategory, String.format("An alert of type '%s' and category '%s' already exists.", alertType, alertCategory));
    }

    public DuplicateAlertException(AlertType alertType, AlertCategory alertCategory, String message) {
        super(message);
        this.alertType = alertType;
        this.alertCategory = alertCategory;
    }

    public AlertType getAlertType() {
        return alertType;
    }

    public AlertCategory getAlertCategory() {
        return alertCategory;
    }
}
