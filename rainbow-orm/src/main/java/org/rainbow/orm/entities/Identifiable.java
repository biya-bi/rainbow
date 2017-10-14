package org.rainbow.orm.entities;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.MappedSuperclass;

/**
 *
 * @author Biya-Bi
 * @param <T>
 *            The type of the Id
 */
@MappedSuperclass
@Access(value = AccessType.PROPERTY)
public abstract class Identifiable<T extends Serializable> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4674915874082623116L;
	private T id;

	public Identifiable() {
	}

	public Identifiable(T id) {
		this.id = id;
	}

	public T getId() {
		return id;
	}

	public void setId(T id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Identifiable<?> other = (Identifiable<?>) obj;
		return Objects.equals(this.getId(), other.getId());
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "[ id=" + getId() + " ]";
	}
}
