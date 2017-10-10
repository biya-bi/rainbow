package org.rainbow.security.service.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class DuplicateGroupException extends RainbowSecurityServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3837992995580583601L;
	private final String groupName;

	public DuplicateGroupException(String groupName) {
		this(groupName, String.format("A group with name '%s' already exists.", groupName));
	}

	public DuplicateGroupException(String groupName, String message) {
		super(message);
		this.groupName = groupName;
	}

	public String getGroupName() {
		return groupName;
	}

}
