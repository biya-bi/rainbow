package org.rainbow.security.faces.controllers;

import static org.rainbow.security.faces.util.ResourceBundles.CRUD_MESSAGES;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.rainbow.faces.util.FacesContextUtil;
import org.rainbow.security.faces.util.CrudNotificationInfo;
import org.rainbow.security.orm.entities.Application;
import org.rainbow.security.orm.entities.Authority;
import org.rainbow.security.orm.entities.Group;
import org.rainbow.security.orm.entities.LockoutPolicy;
import org.rainbow.security.orm.entities.LoginPolicy;
import org.rainbow.security.orm.entities.Membership;
import org.rainbow.security.orm.entities.PasswordPolicy;
import org.rainbow.security.orm.entities.User;
import org.rainbow.security.service.exceptions.DuplicateApplicationException;
import org.rainbow.security.service.services.ApplicationService;
import org.rainbow.service.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@ViewScoped
@CrudNotificationInfo(createdMessageKey = "ApplicationCreated", updatedMessageKey = "ApplicationUpdated", deletedMessageKey = "ApplicationDeleted")
public class ApplicationController extends AuditableController<Application> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7783179356086452428L;

	@Autowired
	private ApplicationService applicationService;

	private static final String APPLICATION_NODE_NAME = "application";
	private static final String NAME_ATTRIBUTE_NAME = "name";
	private static final String USERS_NODE_NAME = "users";
	private static final String USER_NODE_NAME = "user";
	private static final String USER_NAME_NODE_NAME = "username";
	private static final String PASSWORD_NODE_NAME = "password";
	private static final String ENABLED_NODE_NAME = "enables";
	private static final String AUTHORITIES_NODE_NAME = "authorities";
	private static final String AUTHORITY_NODE_NAME = "authority";
	private static final String NAME_NODE_NAME = "name";
	private static final String DESCRIPTION_NODE_NAME = "description";
	private static final String GROUPS_NODE_NAME = "groups";
	private static final String GROUP_NODE_NAME = "group";

	private static final String DUPLICATE_APPLICATION_NAME_ERROR_KEY = "DuplicateApplicationName";

	public ApplicationController() {
		super(Application.class);
	}

	@Override
	public Application prepareCreate() {
		Application application = super.prepareCreate();

		PasswordPolicy passwordPolicy = new PasswordPolicy();
		passwordPolicy.setMaxAge((short)30);
		passwordPolicy.setMaxLength((short)128);
		passwordPolicy.setMinLowercaseCharsCount((short)1);
		passwordPolicy.setMinNumericCount((short)1);
		passwordPolicy.setMinAge((short)0);
		passwordPolicy.setMinLength((short)8);
		passwordPolicy.setMinSpecialCharsCount((short)1);
		passwordPolicy.setMinUppercaseCharsCount((short)1);
		passwordPolicy.setHistoryThreshold((short)10);

		application.setPasswordPolicy(passwordPolicy);

		LockoutPolicy lockoutPolicy = new LockoutPolicy();
		lockoutPolicy.setAttemptWindow((short) 10);
		lockoutPolicy.setThreshold((short) 5);

		application.setLockoutPolicy(lockoutPolicy);

		LoginPolicy loginPolicy = new LoginPolicy();
		loginPolicy.setThreshold((short) 3);

		application.setLoginPolicy(loginPolicy);

		return application;
	}

	public void bulkCreate(FileUploadEvent event)
			throws SAXException, ParserConfigurationException, IOException, Exception {
		UploadedFile file = event.getFile();
		DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

		Document doc = dBuilder.parse(file.getInputstream());

		applicationService.create(getApplications(doc));
	}

	private List<User> getApplicationUsers(Application application, Node applicationNodeChildNode) {
		List<User> users = new ArrayList<>();
		List<User> applicationUsers = application.getUsers();
		if (applicationUsers == null) {
			applicationUsers = new ArrayList<>();
			application.setUsers(applicationUsers);
		}
		NodeList applicationUsersChildNodes = applicationNodeChildNode.getChildNodes();
		for (int userCount = 0; userCount < applicationUsersChildNodes.getLength(); userCount++) {
			Node userNode = applicationUsersChildNodes.item(userCount);
			if (userNode.getNodeType() == Node.ELEMENT_NODE) {
				if (USER_NODE_NAME.toUpperCase().equals(userNode.getNodeName().toUpperCase())) {
					User user = new User();
					applicationUsers.add(user);
					user.setApplication(application);
					Membership membership = new Membership();
					user.setMembership(membership);
					users.add(user);

					NodeList userNodeChildNodes = userNode.getChildNodes();
					for (int userPropCount = 0; userPropCount < userNodeChildNodes.getLength(); userPropCount++) {
						Node userPropertyNode = userNodeChildNodes.item(userPropCount);
						if (userPropertyNode.getNodeType() == Node.ELEMENT_NODE) {
							String upperNodeName = userPropertyNode.getNodeName().toUpperCase();
							if (userPropertyNode.hasChildNodes()) {
								NodeList userPropertyNodeChildNodes = userPropertyNode.getChildNodes();
								Node valueNode = userPropertyNodeChildNodes.item(0);
								String userPropertyNodeValue = valueNode.getNodeValue();
								if (upperNodeName.equals(USER_NAME_NODE_NAME.toUpperCase())) {
									user.setUserName(userPropertyNodeValue);
								} else if (upperNodeName.equals(PASSWORD_NODE_NAME.toUpperCase())) {
									membership.setPassword(userPropertyNodeValue);
								} else if (upperNodeName.equals(ENABLED_NODE_NAME.toUpperCase())) {
									membership.setEnabled(Boolean.valueOf(userPropertyNodeValue));
								}
							}
						}
					}
				}
			}
		}
		return users;
	}

	private List<Authority> getApplicationAuthorities(Application application, Node applicationNodeChildNode) {
		List<Authority> authorities = new ArrayList<>();
		List<Authority> applicationAuthorities = application.getAuthorities();
		if (applicationAuthorities == null) {
			applicationAuthorities = new ArrayList<>();
			application.setAuthorities(applicationAuthorities);
		}
		NodeList applicationUsersChildNodes = applicationNodeChildNode.getChildNodes();
		for (int authorityCount = 0; authorityCount < applicationUsersChildNodes.getLength(); authorityCount++) {
			Node authorityNode = applicationUsersChildNodes.item(authorityCount);
			if (authorityNode.getNodeType() == Node.ELEMENT_NODE) {
				if (AUTHORITY_NODE_NAME.toUpperCase().equals(authorityNode.getNodeName().toUpperCase())) {
					Authority authority = new Authority();
					authority.setApplication(application);
					applicationAuthorities.add(authority);
					authorities.add(authority);

					NodeList authorityNodeChildNodes = authorityNode.getChildNodes();
					for (int authorityPropCount = 0; authorityPropCount < authorityNodeChildNodes
							.getLength(); authorityPropCount++) {
						Node authorityPropertyNode = authorityNodeChildNodes.item(authorityPropCount);
						if (authorityPropertyNode.getNodeType() == Node.ELEMENT_NODE) {
							String upperNodeName = authorityPropertyNode.getNodeName().toUpperCase();
							if (authorityPropertyNode.hasChildNodes()) {
								NodeList authorityPropertyNodeChildNodes = authorityPropertyNode.getChildNodes();
								Node valueNode = authorityPropertyNodeChildNodes.item(0);
								String authorityPropertyNodeValue = valueNode.getNodeValue();
								if (upperNodeName.equals(NAME_NODE_NAME.toUpperCase())) {
									authority.setName(authorityPropertyNodeValue);
								} else if (upperNodeName.equals(DESCRIPTION_NODE_NAME.toUpperCase())) {
									authority.setDescription(authorityPropertyNodeValue);
								}
							}
						}
					}
				}
			}
		}
		return authorities;
	}

	private List<Group> getApplicationGroups(Application application, Node applicationNodeChildNode, List<User> users,
			List<Authority> authorities) {
		List<Group> groups = new ArrayList<>();
		List<Group> applicationGroups = application.getGroups();
		if (applicationGroups == null) {
			applicationGroups = new ArrayList<>();
			application.setGroups(applicationGroups);
		}
		NodeList applicationUsersChildNodes = applicationNodeChildNode.getChildNodes();
		for (int groupCount = 0; groupCount < applicationUsersChildNodes.getLength(); groupCount++) {
			Node groupNode = applicationUsersChildNodes.item(groupCount);
			if (groupNode.getNodeType() == Node.ELEMENT_NODE) {
				if (GROUP_NODE_NAME.toUpperCase().equals(groupNode.getNodeName().toUpperCase())) {
					Group group = new Group();
					applicationGroups.add(group);
					group.setApplication(application);
					groups.add(group);

					NodeList groupNodeChildNodes = groupNode.getChildNodes();
					for (int groupPropCount = 0; groupPropCount < groupNodeChildNodes.getLength(); groupPropCount++) {
						Node groupPropertyNode = groupNodeChildNodes.item(groupPropCount);
						if (groupPropertyNode.getNodeType() == Node.ELEMENT_NODE) {
							String upperNodeName = groupPropertyNode.getNodeName().toUpperCase();
							if (groupPropertyNode.hasChildNodes()) {
								NodeList groupPropertyNodeChildNodes = groupPropertyNode.getChildNodes();
								if (upperNodeName.equals(NAME_NODE_NAME.toUpperCase())) {
									group.setName(groupPropertyNodeChildNodes.item(0).getNodeValue());
								} else if (upperNodeName.equals(DESCRIPTION_NODE_NAME.toUpperCase())) {
									group.setDescription(groupPropertyNodeChildNodes.item(0).getNodeValue());
								} else if (upperNodeName.equals(USERS_NODE_NAME.toUpperCase())) {
									if (users != null) {
										group.setUsers(getGroupUsers(group, groupPropertyNode, users));
									}
								} else if (upperNodeName.equals(AUTHORITIES_NODE_NAME.toUpperCase())) {
									if (authorities != null) {
										group.setAuthorities(
												getGroupAuthorities(group, groupPropertyNode, authorities));
									}
								}
							}
						}
					}
				}
			}
		}
		return groups;
	}

	private List<User> getGroupUsers(Group group, Node groupNodeChildNode, List<User> users) {
		List<User> groupUsers = new ArrayList<>();
		NodeList groupUsersChildNodes = groupNodeChildNode.getChildNodes();
		for (int userCount = 0; userCount < groupUsersChildNodes.getLength(); userCount++) {
			Node userNode = groupUsersChildNodes.item(userCount);
			if (userNode.getNodeType() == Node.ELEMENT_NODE) {
				if (USER_NODE_NAME.toUpperCase().equals(userNode.getNodeName().toUpperCase())) {
					NodeList userNodeChildNodes = userNode.getChildNodes();
					for (int userPropCount = 0; userPropCount < userNodeChildNodes.getLength(); userPropCount++) {
						Node userPropertyNode = userNodeChildNodes.item(userPropCount);
						if (userPropertyNode.getNodeType() == Node.ELEMENT_NODE) {
							String upperNodeName = userPropertyNode.getNodeName().toUpperCase();
							if (userPropertyNode.hasChildNodes()) {
								NodeList userPropertyNodeChildNodes = userPropertyNode.getChildNodes();
								Node valueNode = userPropertyNodeChildNodes.item(0);
								String userPropertyNodeValue = valueNode.getNodeValue();
								if (upperNodeName.equals(USER_NAME_NODE_NAME.toUpperCase())) {
									if (userPropertyNodeValue != null) {
										for (User user : users) {
											if (user.getUserName() != null && userPropertyNodeValue.toUpperCase()
													.equals(user.getUserName().toUpperCase())) {
												groupUsers.add(user);
												List<Group> userGroups = user.getGroups();
												if (userGroups == null) {
													userGroups = new ArrayList<>();
													user.setGroups(userGroups);
												}
												userGroups.add(group);
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return groupUsers;
	}

	private List<Authority> getGroupAuthorities(Group group, Node groupNodeChildNode, List<Authority> authorities) {
		List<Authority> groupAuthorities = new ArrayList<>();
		NodeList groupAuthoritiesChildNodes = groupNodeChildNode.getChildNodes();
		for (int authorityCount = 0; authorityCount < groupAuthoritiesChildNodes.getLength(); authorityCount++) {
			Node authorityNode = groupAuthoritiesChildNodes.item(authorityCount);
			if (authorityNode.getNodeType() == Node.ELEMENT_NODE) {
				if (AUTHORITY_NODE_NAME.toUpperCase().equals(authorityNode.getNodeName().toUpperCase())) {
					NodeList authorityNodeChildNodes = authorityNode.getChildNodes();
					for (int authorityPropCount = 0; authorityPropCount < authorityNodeChildNodes
							.getLength(); authorityPropCount++) {
						Node authorityPropertyNode = authorityNodeChildNodes.item(authorityPropCount);
						if (authorityPropertyNode.getNodeType() == Node.ELEMENT_NODE) {
							String upperNodeName = authorityPropertyNode.getNodeName().toUpperCase();
							if (authorityPropertyNode.hasChildNodes()) {
								NodeList authorityPropertyNodeChildNodes = authorityPropertyNode.getChildNodes();
								Node valueNode = authorityPropertyNodeChildNodes.item(0);
								String authorityPropertyNodeValue = valueNode.getNodeValue();
								if (upperNodeName.equals(NAME_NODE_NAME.toUpperCase())) {
									if (authorityPropertyNodeValue != null) {
										for (Authority authority : authorities) {
											if (authority.getName() != null && authorityPropertyNodeValue.toUpperCase()
													.equals(authority.getName().toUpperCase())) {
												groupAuthorities.add(authority);
												List<Group> authorityGroups = authority.getGroups();
												if (authorityGroups == null) {
													authorityGroups = new ArrayList<>();
													authority.setGroups(authorityGroups);
												}
												authorityGroups.add(group);
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return groupAuthorities;
	}

	private List<Application> getApplications(Document doc) throws Exception {
		List<Application> applications = new ArrayList<>();

		if (!doc.hasChildNodes()) {
			return applications; // TODO: Throw an exception here
		}

		NodeList nodeList = doc.getChildNodes();

		for (int count = 0; count < nodeList.getLength(); count++) {

			Node applicationNode = nodeList.item(count);
			if (applicationNode.getNodeType() == Node.ELEMENT_NODE) {
				if (APPLICATION_NODE_NAME.toUpperCase().equals(applicationNode.getNodeName().toUpperCase())) {
					Application application = prepareCreate();

					List<User> users = null;
					List<Authority> authorities = null;
					List<Group> groups = null;

					if (applicationNode.hasAttributes()) {
						NamedNodeMap applicationNamedNodeMap = applicationNode.getAttributes();
						for (int applicationNamedNodeMapCount = 0; applicationNamedNodeMapCount < applicationNamedNodeMap
								.getLength(); applicationNamedNodeMapCount++) {
							Node attr = applicationNamedNodeMap.item(applicationNamedNodeMapCount);
							if (NAME_ATTRIBUTE_NAME.toUpperCase().equals(attr.getNodeName().toUpperCase())) {
								application.setName(attr.getNodeValue());
							}
						}
					}
					if (applicationNode.hasChildNodes()) {

						NodeList applicationNodeChildNodes = applicationNode.getChildNodes();
						for (int applicationNodeChildNodesCount = 0; applicationNodeChildNodesCount < applicationNodeChildNodes
								.getLength(); applicationNodeChildNodesCount++) {
							Node applicationNodeChildNode = applicationNodeChildNodes
									.item(applicationNodeChildNodesCount);
							if (USERS_NODE_NAME.toUpperCase()
									.equals(applicationNodeChildNode.getNodeName().toUpperCase())) {
								users = getApplicationUsers(application, applicationNodeChildNode);
							} else if (AUTHORITIES_NODE_NAME.toUpperCase()
									.equals(applicationNodeChildNode.getNodeName().toUpperCase())) {
								authorities = getApplicationAuthorities(application, applicationNodeChildNode);
							} else if (GROUPS_NODE_NAME.toUpperCase()
									.equals(applicationNodeChildNode.getNodeName().toUpperCase())) {
								groups = getApplicationGroups(application, applicationNodeChildNode, users,
										authorities);
							}
						}
					}
					if (users != null) {
						application.setUsers(users);
					}
					if (groups != null) {
						application.setGroups(groups);
					}
					if (authorities != null) {
						application.setAuthorities(authorities);
					}
					applications.add(application);
				}
			}
		}
		return applications;
	}

	@Override
	protected boolean handle(Throwable throwable) {
		if (throwable instanceof DuplicateApplicationException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			DuplicateApplicationException e = (DuplicateApplicationException) throwable;
			FacesContextUtil.addErrorMessage(String.format(
					ResourceBundle.getBundle(CRUD_MESSAGES).getString(DUPLICATE_APPLICATION_NAME_ERROR_KEY),
					e.getName()));
			return true;
		}
		return super.handle(throwable);
	}

	@Override
	protected Service<Application> getService() {
		return applicationService;
	}
}
