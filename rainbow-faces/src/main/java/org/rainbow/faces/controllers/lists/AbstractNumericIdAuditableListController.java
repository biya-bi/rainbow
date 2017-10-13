package org.rainbow.faces.controllers.lists;

import java.util.Objects;

import org.rainbow.orm.entities.Trackable;

public abstract class AbstractNumericIdAuditableListController<TModel extends Trackable<TKey>, TKey extends Number>
		extends AbstractAuditableListController<TModel> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4527984494404862322L;

	private Class<TKey> keyClass;

	public AbstractNumericIdAuditableListController(Class<TKey> keyClass) {
		super();
		this.keyClass = Objects.requireNonNull(keyClass);
	}

	@Override
	protected Object convert(String rowKey) {
		return toNumber(rowKey);
	}

	private Number toNumber(String s) {
		if (this.keyClass == Byte.class) {
			return Byte.valueOf(s);
		}
		if (this.keyClass == Double.class) {
			return Double.valueOf(s);
		}
		if (this.keyClass == Float.class) {
			return Float.valueOf(s);
		}
		if (this.keyClass == Integer.class) {
			return Integer.valueOf(s);
		}
		if (this.keyClass == Long.class) {
			return Long.valueOf(s);
		}
		if (this.keyClass == Short.class) {
			return Short.valueOf(s);
		}
		throw new IllegalStateException("Number is not assignable from the key class.");
	}
}
