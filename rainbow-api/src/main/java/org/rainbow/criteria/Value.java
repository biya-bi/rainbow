package org.rainbow.criteria;

public interface Value<T> extends Expression<T> {
	T get();
}
