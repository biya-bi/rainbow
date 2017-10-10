package org.rainbow.faces.filters;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Biya-Bi
 */
@XmlRootElement
public class RangeValuedFilter<T extends Comparable<? super T>> extends Filter<T> {

	private T lowerBound;
	private T upperBound;

	public RangeValuedFilter() {
	}

	public RangeValuedFilter(String fieldName) {
		super(fieldName);
	}

	public RangeValuedFilter(String fieldName, RelationalOperator operator, T lowerBound, T upperBound) {
		super(fieldName, operator);
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
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
