package org.rainbow.persistence;

import java.util.List;

/**
 *
 * @author Biya-Bi
 */
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
