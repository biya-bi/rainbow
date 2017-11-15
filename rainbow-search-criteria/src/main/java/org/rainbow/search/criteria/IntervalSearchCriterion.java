package org.rainbow.search.criteria;

/**
 *
 * @author Biya-Bi
 */
public class IntervalSearchCriterion<T extends Comparable<? super T>> extends AbstractSearchCriterion {
	public static enum Operator {
		LEFT_OPEN_RIGHT_OPEN, LEFT_OPEN_RIGHT_CLOSED, LEFT_CLOSED_RIGHT_OPEN, LEFT_CLOSED_RIGHT_CLOSED, LEFT_OPEN_RIGHT_OPEN_COMPLIMENT, LEFT_OPEN_RIGHT_CLOSED_COMPLIMENT, LEFT_CLOSED_RIGHT_OPEN_COMPLIMENT, LEFT_CLOSED_RIGHT_CLOSED_COMPLIMENT
	}

	private Operator operator;
	private T lowerBound;
	private T upperBound;

	public IntervalSearchCriterion() {
	}

	public IntervalSearchCriterion(String path) {
		super(path);
	}

	public IntervalSearchCriterion(String path, Operator operator, T lowerBound, T upperBound) {
		super(path);
		this.operator = operator;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public T getLowerBound() {
		return lowerBound;
	}

	public void setLowerBound(T value) {
		this.lowerBound = value;
	}

	public T getUpperBound() {
		return upperBound;
	}

	public void setUpperBound(T upperBound) {
		this.upperBound = upperBound;
	}

}
