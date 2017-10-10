package org.rainbow.faces.filters;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Biya-Bi
 */
public class SearchOptions implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -7777437712976039176L;
	private Integer pageIndex;
    private Integer pageSize;
    private List<Filter<?>> filters;

    public SearchOptions() {
    }

    public SearchOptions(Integer pageIndex, Integer pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<Filter<?>> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter<?>> filters) {
        this.filters = filters;
    }
}
