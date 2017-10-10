package org.rainbow.faces.filters;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Biya-Bi
 */
@XmlRootElement
public abstract class Filter<T> {

    private String path;
    private RelationalOperator operator;

    public Filter() {
    }

    public Filter(String path) {
        this.path = path;
    }

    public Filter(String path, RelationalOperator operator) {
        this.path = path;
        this.operator = operator;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String fieldName) {
        this.path = fieldName;
    }

    public RelationalOperator getOperator() {
        return operator;
    }

    public void setOperator(RelationalOperator operator) {
        this.operator = operator;
    }

}
