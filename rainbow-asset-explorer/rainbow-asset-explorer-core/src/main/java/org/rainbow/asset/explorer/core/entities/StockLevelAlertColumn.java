/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.asset.explorer.core.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Biya-Bi
 */
@Entity
@Table(name = "STOCK_LEVEL_ALERT_COLUMN", uniqueConstraints = @UniqueConstraint(columnNames = {"COLUMN_TYPE", "LOCALE_ID"}))
public class StockLevelAlertColumn extends Identifiable<Integer> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7585098757114512283L;
	private StockLevelAlertColumnType columnType;
    private Locale locale;
    private String headerText;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Override
    public Integer getId() {
        return super.getId();
    }

    @Override
    public void setId(Integer id) {
        super.setId(id);
    }

    @NotNull
    @Column(name = "COLUMN_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    public StockLevelAlertColumnType getColumnType() {
        return columnType;
    }

    public void setColumnType(StockLevelAlertColumnType columnType) {
        this.columnType = columnType;
    }

    @NotNull
    @JoinColumn(name = "LOCALE_ID", nullable = false)
    @ManyToOne
    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @NotNull
    @Column(name = "HEADER_TEXT", nullable = false)
    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }

}
