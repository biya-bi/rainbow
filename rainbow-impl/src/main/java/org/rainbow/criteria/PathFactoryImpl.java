package org.rainbow.criteria;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.function.Function;

import org.rainbow.criteria.Path;
import org.rainbow.criteria.PathFactory;

public class PathFactoryImpl implements PathFactory {

	@Override
	public Path create(String attributeName) {
		return new PathImpl(attributeName);
	}

	@Override
	public <T> Path create(Class<T> cls, Function<? super T, String> func) {
		Objects.requireNonNull(cls);
		Objects.requireNonNull(func);
		return new PathImpl(Objects.requireNonNull(func.apply(instantiate(cls))));
	}

	private <T> T instantiate(Class<T> cls) {
		Objects.requireNonNull(cls);
		try {
			return cls.getConstructor().newInstance(new Object[] {});
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}
}
