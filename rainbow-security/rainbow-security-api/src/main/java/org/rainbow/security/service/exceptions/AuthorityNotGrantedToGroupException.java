package org.rainbow.security.service.exceptions;

/**
 *
 * @author Biya-Bi
 */
public class AuthorityNotGrantedToGroupException extends RainbowSecurityServiceException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2290237296208742324L;
	private final String authorityName;
	private final String groupName;

	public AuthorityNotGrantedToGroupException(String authorityName, String groupName) {
		this(authorityName, groupName,
				String.format("The authority '%s' has not been granted to the group '%s'.", authorityName, groupName));
	}

	public AuthorityNotGrantedToGroupException(String authorityName, String groupName, String message) {
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
