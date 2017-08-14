package org.rainbow.asset.explorer.core.audit;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.rainbow.asset.explorer.core.entities.Currency;

@Entity
@Table(name = "CURRENCY_AUDIT")
public class CurrencyAudit extends TrackableAudit<Currency, Integer> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4811816870629425516L;
	private String name;
    private String symbol;
    private String description;
    
    public CurrencyAudit() {
    }

    public CurrencyAudit(Currency currency, WriteOperation writeOperation) {
        super(currency, writeOperation);
        this.name = currency.getName();
        this.symbol = currency.getSymbol();
        this.description = currency.getDescription();
    }

    @Basic(optional = false)
    @NotNull
    @Size(min = 1)
    @Column(nullable = false, unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Column(columnDefinition = "TEXT")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
