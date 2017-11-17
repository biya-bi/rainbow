package org.rainbow.journal.service.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class DuplicateJournalException extends RainbowJournalException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2915299406565044095L;
	private final String name;

	public DuplicateJournalException(String name) {
		this(name, String.format("A journal name '%s' already exists.", name));
	}

	public DuplicateJournalException(String name, String message) {
		super(message);
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
