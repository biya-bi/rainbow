package org.rainbow.search.criteria;

/**
 * 
 * @author Biya-Bi
 */
public class ComparableSearchCriterion<T extends Comparable<? super T>> extends AbstractSearchCriterion {

	public static enum Operator {
		EQUAL, NOT_EQUAL, LESS_THAN, LESS_THAN_OR_EQUAL, GREATER_THAN, GREATER_THAN_OR_EQUAL, IS_EMPTY, IS_NOT_EMPTY
	}

	private Operator operator;

	private T value;

	public ComparableSearchCriterion() {
	}

	public ComparableSearchCriterion(String path) {
		super(path);
	}

	public ComparableSearchCriterion(String path, Operator operator, T value) {
		super(path);
		this.operator = operator;
		this.value = value;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

}
