package org.rainbow.search.criteria;

import java.util.List;

/**
 *
 * @author Biya-Bi
 */
public class ListSearchCriterion<T extends Comparable<? super T>> extends AbstractSearchCriterion {
	
	public static enum Operator {
		IN, NOT_IN
	}

	private Operator operator;
	private List<T> list;

	public ListSearchCriterion() {
	}

	public ListSearchCriterion(String path) {
		super(path);
	}

	public ListSearchCriterion(String path, Operator operator, List<T> value) {
		super(path);
		this.operator = operator;
		this.list = value;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

}
