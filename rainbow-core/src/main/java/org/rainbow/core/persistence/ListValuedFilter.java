/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.core.persistence;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Biya-Bi
 */
@XmlRootElement
public class ListValuedFilter<T extends Comparable<? super T>> extends Filter<T> {

	private List<T> list;

	public ListValuedFilter() {
	}

	public ListValuedFilter(String fieldName) {
		super(fieldName);
	}

	public ListValuedFilter(String fieldName, RelationalOperator operator, List<T> value) {
		super(fieldName, operator);
		this.list = value;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

}
