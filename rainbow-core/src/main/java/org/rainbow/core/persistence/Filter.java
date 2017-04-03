/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.core.persistence;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Biya-Bi
 */
@XmlRootElement
public abstract class Filter<T> {

    private String fieldName;
    private RelationalOperator operator;

    public Filter() {
    }

    public Filter(String fieldName) {
        this.fieldName = fieldName;
    }

    public Filter(String fieldName, RelationalOperator operator) {
        this.fieldName = fieldName;
        this.operator = operator;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public RelationalOperator getOperator() {
        return operator;
    }

    public void setOperator(RelationalOperator operator) {
        this.operator = operator;
    }

}
