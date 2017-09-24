package org.rainbow.security.service.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class GroupNotFoundException extends RainbowSecurityServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 997404608841900672L;
	private final String groupName;

	public GroupNotFoundException(String groupName) {
		this(groupName, String.format("No group with name '%s' was found.", groupName));
	}

	public GroupNotFoundException(String groupName, String message) {
		super(message);
		this.groupName = groupName;
	}

	public String getGroupName() {
		return groupName;
	}
}
