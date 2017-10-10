package org.rainbow.criteria;

public class ValueImpl<T> extends ExpressionImpl<T> implements Value<T> {
	private T value;

	public ValueImpl(T value) {
		super();
		this.value = value;
	}

	@Override
	public T get() {
		return value;
	}
}
