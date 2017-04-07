/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rainbow.journal.ui.web.controller;

import static org.rainbow.journal.ui.web.utilities.ResourceBundles.CRUD_MESSAGES;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.rainbow.core.persistence.Filter;
import org.rainbow.core.persistence.RelationalOperator;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.persistence.SingleValuedFilter;
import org.rainbow.core.service.Service;
import org.rainbow.journal.ui.web.utilities.JsfUtil;
import org.rainbow.security.core.entities.Application;
import org.rainbow.security.core.entities.Group;
import org.rainbow.security.core.entities.Membership;
import org.rainbow.security.core.entities.User;
import org.rainbow.security.core.persistence.exceptions.DuplicateUserException;
import org.rainbow.security.core.service.UserGroupService;
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
	
	@Value("${default.store.web.user.group.name}")
	private String defaultStoreWebUserGroupName;
	
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

		return "success";
	}

	private Application getApplication() throws Exception {
		SearchOptions criteria = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.EQUAL);
		filter.setValue(applicationName);
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Application> result = applicationService.find(criteria);
		if (result != null && result.size() == 1)
			return result.get(0);
		return null;
	}

	private Group getGroup() throws Exception {
		SearchOptions criteria = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>();
		filter.setFieldName("name");
		filter.setOperator(RelationalOperator.EQUAL);
		filter.setValue(defaultStoreWebUserGroupName);
		List<Filter<?>> filters = new ArrayList<>();
		filters.add(filter);
		criteria.setFilters(filters);
		List<Group> result = groupService.find(criteria);
		if (result != null && result.size() == 1)
			return result.get(0);
		return null;
	}
}
