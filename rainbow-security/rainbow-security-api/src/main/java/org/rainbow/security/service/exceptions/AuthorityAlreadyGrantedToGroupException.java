package org.rainbow.security.service.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class AuthorityAlreadyGrantedToGroupException extends RainbowSecurityServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6090158351207501151L;
	private final String authorityName;
	private final String groupName;

	public AuthorityAlreadyGrantedToGroupException(String authorityName, String groupName) {
		this(authorityName, groupName,
				String.format("The authority '%s' has already been granted to the group '%s'.", authorityName, groupName));
	}

	public AuthorityAlreadyGrantedToGroupException(String authorityName, String groupName, String message) {
		super(message);
		this.authorityName = authorityName;
		this.groupName = groupName;
	}

	public String getAuthorityName() {
		return authorityName;
	}

	public String getGroupName() {
		return groupName;
	}

}
