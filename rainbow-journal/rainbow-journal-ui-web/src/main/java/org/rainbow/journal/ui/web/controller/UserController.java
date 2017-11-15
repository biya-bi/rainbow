/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.journal.ui.web.controller;

import static org.rainbow.journal.ui.web.utilities.ResourceBundles.CRUD_MESSAGES;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.rainbow.core.persistence.SearchCriterion;
import org.rainbow.core.persistence.RelationalOperator;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.persistence.SingleValuedSearchCriterion;
import org.rainbow.journal.core.entities.Profile;
import org.rainbow.journal.ui.web.utilities.JsfUtil;
import org.rainbow.security.core.entities.Application;
import org.rainbow.security.core.entities.Group;
import org.rainbow.security.core.entities.Membership;
import org.rainbow.security.core.entities.User;
import org.rainbow.security.core.persistence.exceptions.DuplicateUserException;
import org.rainbow.security.core.service.UserGroupService;
import org.rainbow.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 *
 * @author Biya-Bi
 */
@Component
@Named
@ViewScoped
public class UserController extends TrackableController<User, Long, SearchOptions> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 798332191079083979L;

	@Autowired
	@Qualifier("userService")
	private Service<User, Long, SearchOptions> userService;

	@Autowired
	@Qualifier("applicationService")
	private Service<Application, Long, SearchOptions> applicationService;

	@Autowired
	@Qualifier("groupService")
	private Service<Group, Long, SearchOptions> groupService;

	@Autowired
	@Qualifier("userGroupService")
	private UserGroupService userGroupService;

	@Autowired
	@Qualifier("authenticationManager")
	private AuthenticationManager authenticationManager;

	@Value("${application.name}")
	private String applicationName;

	@Value("${default.journal.web.user.group.name}")
	private String defaultJournalWebUserGroupName;

	@Autowired
	@Qualifier("profileService")
	private Service<Profile, Long, SearchOptions> profileService;

	private final Profile profile = new Profile();

	private static final String DUPLICATE_USER_NAME_ERROR_KEY = "DuplicateUserName";

	public UserController() {
		super(User.class);
	}

	@Override
	protected Service<User, Long, SearchOptions> getService() {
		return userService;
	}

	@Override
	protected boolean handle(Throwable throwable) {
		if (throwable instanceof DuplicateUserException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			DuplicateUserException e = (DuplicateUserException) throwable;
			JsfUtil.addErrorMessage(String.format(
					ResourceBundle.getBundle(CRUD_MESSAGES).getString(DUPLICATE_USER_NAME_ERROR_KEY), e.getUserName()));
			return true;
		}
		return super.handle(throwable);
	}

	public void init() {
		User user = super.prepareCreate();
		Membership membership = new Membership();
		membership.setEnabled(true);
		user.setMembership(membership);
		membership.setUser(user);
	}

	public String signup() throws Exception {
		User current = this.getCurrent();

		Application application = getApplication();
		current.setApplication(application);
		Group group = getGroup();

		// It is important to save the unencrypted password in a local variable
		// because once the create method is called, the user's password will be
		// encrypted and we won't be able to use it to authenticate the user at
		// the end of this method.
		String password = current.getMembership().getPassword();

		super.create();

		userGroupService.addUsersToGroups(Arrays.asList(new Long[] { current.getId() }),
				Arrays.asList(new Long[] { group.getId() }), application.getId());

		SecurityContextHolder.getContext().setAuthentication(authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(current.getUserName(), password)));

		profile.setUserName(current.getUserName());
		profile.setEmail(current.getMembership().getEmail());
		profile.setCreator(current.getUserName());
		profile.setUpdater(current.getUserName());

		profileService.create(profile);

		return "success";
	}

	private Application getApplication() throws Exception {
		SearchOptions options = new SearchOptions();

		StringSearchCriterion nameSearchCriterion = new SingleValuedFilter<>("name", RelationalOperator.EQUAL,
				applicationName);

		options.setFilters(Arrays.asList(new Filter<?>[] { nameFilter }));

		List<Application> result = applicationService.find(options);
		if (result != null && result.size() == 1)
			return result.get(0);
		throw new IllegalStateException(String.format("No application with name '%s' was found.", applicationName));
	}

	private Group getGroup() throws Exception {
		SearchOptions options = new SearchOptions();

		StringSearchCriterion groupNameSearchCriterion = new SingleValuedFilter<>("name", RelationalOperator.EQUAL,
				defaultJournalWebUserGroupName);
		StringSearchCriterion applicationNameSearchCriterion = new SingleValuedFilter<>("application.name",
				RelationalOperator.EQUAL, applicationName);

		options.setFilters(Arrays.asList(new Filter<?>[] { groupNameFilter, applicationNameFilter }));

		List<Group> result = groupService.find(options);
		if (result != null && result.size() == 1)
			return result.get(0);
		throw new IllegalStateException(
				String.format("No group with name '%s' has been found in the application with name '%s'.",
						defaultJournalWebUserGroupName, applicationName));
	}

	public Profile getProfile() {
		return profile;
	}

}
