/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.core.persistence;

import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Biya-Bi
 */
@XmlRootElement
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
