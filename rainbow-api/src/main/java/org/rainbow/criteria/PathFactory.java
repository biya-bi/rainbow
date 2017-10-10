package org.rainbow.criteria;

import java.util.function.Function;

public interface PathFactory {
	Path create(String attributeName);

	<T> Path create(Class<T> cls, Function<? super T, String> func);
}
