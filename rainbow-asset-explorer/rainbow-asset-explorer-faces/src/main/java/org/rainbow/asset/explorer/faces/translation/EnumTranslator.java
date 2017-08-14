/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.faces.translation;

import java.util.ResourceBundle;

import org.rainbow.asset.explorer.core.entities.AlertCategory;
import org.rainbow.asset.explorer.core.entities.AlertType;
import org.rainbow.asset.explorer.core.entities.AssetState;
import org.rainbow.asset.explorer.core.entities.DepreciationMethod;
import org.rainbow.asset.explorer.core.entities.DocumentType;
import org.rainbow.asset.explorer.core.entities.ManufacturerBusinessImpact;
import org.rainbow.asset.explorer.core.entities.PurchaseOrderStatus;
import org.rainbow.asset.explorer.core.entities.ShippingOrderStatus;
import org.rainbow.asset.explorer.core.entities.SiteStatus;
import org.rainbow.asset.explorer.faces.utilities.ResourceBundles;

/**
 *
 * @author Biya-Bi
 */
public class EnumTranslator {

    private final ResourceBundle resourceBundle;

    public EnumTranslator() {
        resourceBundle = ResourceBundle.getBundle(ResourceBundles.LABELS);
    }

    public String translate(PurchaseOrderStatus status) {
        if (status == null) {
            return null;
        }
        switch (status) {
            case PENDING:
                return resourceBundle.getString("Pending");
            case APPROVED:
                return resourceBundle.getString("Approved");
            case REJECTED:
                return resourceBundle.getString("Rejected");
            case COMPLETE:
                return resourceBundle.getString("Complete");
            default:
                return null;
        }
    }

    public String translate(ShippingOrderStatus status) {
        if (status == null) {
            return null;
        }
        switch (status) {
            case PENDING:
                return resourceBundle.getString("Pending");
            case APPROVED:
                return resourceBundle.getString("Approved");
            case REJECTED:
                return resourceBundle.getString("Rejected");
            case IN_TRANSIT:
                return resourceBundle.getString("InTransit");
            case RESTITUTED:
                return resourceBundle.getString("Restituted");
            case DELIVERED:
                return resourceBundle.getString("Delivered");
            default:
                return null;
        }
    }

    public String translate(AlertCategory alertCategory) {
        if (alertCategory == null) {
            return null;
        }
        switch (alertCategory) {
            case STOCK_LEVEL:
                return resourceBundle.getString("StockLevel");
            default:
                return null;
        }
    }

    public String translate(AlertType alertType) {
        if (alertType == null) {
            return null;
        }
        switch (alertType) {
            case RECOVERY:
                return resourceBundle.getString("Recovery");
            case WARNING:
                return resourceBundle.getString("Warning");
            default:
                return null;
        }
    }

    public String translate(SiteStatus status) {
        if (status == null) {
            return null;
        }
        switch (status) {
            case NEW:
                return resourceBundle.getString("SiteStatusNew");
            case ACTIVE:
                return resourceBundle.getString("SiteStatusActive");
            case DECOMMISSIONED:
                return resourceBundle.getString("SiteStatusDecommissioned");
            default:
                return null;
        }
    }

    public String translate(DocumentType documentType) {
        if (documentType == null) {
            return null;
        }
        switch (documentType) {
            case FINANCIAL_DOCUMENT:
                return resourceBundle.getString("DocumentTypeFinancial");
            case LEGAL_DOCUMENT:
                return resourceBundle.getString("DocumentTypeLegal");
            case WARRANTY:
                return resourceBundle.getString("DocumentTypeWarranty");
            default:
                return null;
        }
    }

    public String translate(AssetState assetState) {
        if (assetState == null) {
            return null;
        }
        switch (assetState) {
            case DISPOSED:
                return resourceBundle.getString("AssetStateDisposed");
            case EXPIRED:
                return resourceBundle.getString("AssetStateExpired");
            case IN_REPAIR:
                return resourceBundle.getString("AssetStateInRepair");
            case IN_USE:
                return resourceBundle.getString("AssetStateInUse");
            default:
                return null;
        }
    }

    public String translate(DepreciationMethod depreciationMethod) {
        if (depreciationMethod == null) {
            return null;
        }
        switch (depreciationMethod) {
            case DECLINING_BALANCE:
                return resourceBundle.getString("DepreciationMethodDecliningBalance");
            case STRAIGHT_LINE:
                return resourceBundle.getString("DepreciationMethodStraightLine");
            case SUM_OF_YEAR_DIGIT:
                return resourceBundle.getString("DepreciationMethodSumOfYearDigit");
            default:
                return null;
        }
    }

    public String translate(ManufacturerBusinessImpact impact) {
        if (impact == null) {
            return null;
        }
        switch (impact) {
            case HIGH:
                return resourceBundle.getString("ManufacturerBusinessImpactHigh");
            case LOW:
                return resourceBundle.getString("ManufacturerBusinessImpactLow");
            case MEDIUM:
                return resourceBundle.getString("ManufacturerBusinessImpactMedium");
            default:
                return null;
        }
    }
}
