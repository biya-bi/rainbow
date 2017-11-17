package org.rainbow.journal.server.dto;

import java.io.Serializable;

public class Dto<T extends Serializable> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3613447864776714649L;
	private T id;

	public Dto() {
	}

	public Dto(T id) {
		this.id = id;
	}

	public T getId() {
		return id;
	}

	public void setId(T id) {
		this.id = id;
	}

}
