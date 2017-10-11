package org.rainbow.faces.filters;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Biya-Bi
 */
@XmlRootElement
public class SingleValuedFilter<T extends Comparable<? super T>> extends Filter<T> {

	private T value;

	public SingleValuedFilter() {
	}

	public SingleValuedFilter(String path) {
		super(path);
	}

	public SingleValuedFilter(String path, RelationalOperator operator, T value) {
		super(path, operator);
		this.value = value;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

}
