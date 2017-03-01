/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.persistence;

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
