package org.rainbow.search.criteria;

/**
 *
 * @author Biya-Bi
 */
public abstract class AbstractSearchCriterion {

    private String path;

    public AbstractSearchCriterion() {
    }

    public AbstractSearchCriterion(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String fieldName) {
        this.path = fieldName;
    }

}
