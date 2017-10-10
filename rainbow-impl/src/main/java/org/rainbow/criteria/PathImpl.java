package org.rainbow.criteria;

import java.util.Objects;

import org.rainbow.criteria.Path;

public class PathImpl implements Path, Expression<String> {
	private final String attributeName;

	public PathImpl(String attributeName) {
		super();
		this.attributeName = Objects.requireNonNull(attributeName);
	}

	@Override
	public String get() {
		return attributeName;
	}

}
