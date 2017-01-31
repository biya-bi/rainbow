/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.shopping.cart.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Id;
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

	@Id
	public T getId() {
		return id;
	}

	public void setId(T id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 79 * hash + Objects.hashCode(this.getId());
		return hash;
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
}
