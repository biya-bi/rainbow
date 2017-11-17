package org.rainbow.journal.faces.controllers.write;

import static org.rainbow.journal.faces.util.ResourceBundles.CRUD_MESSAGES;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.view.ViewScoped;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.rainbow.criteria.PathFactory;
import org.rainbow.criteria.PredicateBuilder;
import org.rainbow.criteria.PredicateBuilderFactory;
import org.rainbow.criteria.SearchOptions;
import org.rainbow.criteria.SearchOptionsFactory;
import org.rainbow.faces.controllers.write.AbstractWriteController;
import org.rainbow.faces.util.CrudNotificationInfo;
import org.rainbow.faces.util.FacesContextUtil;
import org.rainbow.journal.faces.util.ResourceBundles;
import org.rainbow.journal.orm.entities.Profile;
import org.rainbow.journal.service.services.ProfileService;
import org.rainbow.security.orm.entities.Application;
import org.rainbow.security.orm.entities.Group;
import org.rainbow.security.orm.entities.Membership;
import org.rainbow.security.orm.entities.User;
import org.rainbow.security.service.exceptions.DuplicateUserException;
import org.rainbow.security.service.services.ApplicationService;
import org.rainbow.security.service.services.GroupService;
import org.rainbow.security.service.services.UserGroupService;
import org.rainbow.security.service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Named
@ViewScoped
@CrudNotificationInfo(baseName = ResourceBundles.CRUD_MESSAGES, createdMessageKey = "UserCreated", updatedMessageKey = "UserUpdated", deletedMessageKey = "UserDeleted")
public class UserWriteController extends AbstractWriteController<User> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 798332191079083979L;

	@Autowired
	@Qualifier("userService")
	private UserService userService;

	@Autowired
	@Qualifier("applicationService")
	private ApplicationService applicationService;

	@Autowired
	@Qualifier("groupService")
	private GroupService groupService;

	@Autowired
	@Qualifier("authenticationManager")
	private AuthenticationManager authenticationManager;

	@Autowired
	@Qualifier("rainbowJournalApplicationName")
	private String applicationName;

	@Autowired
	@Qualifier("rainbowJournalWebUsersGroupName")
	private String journalWebUsersGroupName;

	@Autowired
	@Qualifier("profileService")
	private ProfileService profileService;

	@Autowired
	@Qualifier("userGroupService")
	private UserGroupService userGroupService;

	@Autowired
	private PathFactory pathFactory;

	@Autowired
	private PredicateBuilderFactory predicateBuilderFactory;

	@Autowired
	private SearchOptionsFactory searchOptionsFactory;

	private final Profile profile = new Profile();

	private static final String DUPLICATE_USER_NAME_ERROR_KEY = "DuplicateUserName";

	public UserWriteController() {
		super(User.class);
	}

	@Override
	public UserService getService() {
		return userService;
	}

	@Override
	protected boolean handle(Throwable throwable) {
		if (throwable instanceof DuplicateUserException) {
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, throwable);
			DuplicateUserException e = (DuplicateUserException) throwable;
			FacesContextUtil.addErrorMessage(String.format(
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
		try {
			User user = this.getModel();

			Application application = getApplication();
			user.setApplication(application);

			// It is important to save the unencrypted password in a local
			// variable
			// because once the create method is called, the user's password
			// will be
			// encrypted and we won't be able to use it to authenticate the user
			// at
			// the end of this method.
			String password = user.getMembership().getPassword();

			userService.create(user);

			Group group = getGroup();

			// user.setGroups(Arrays.asList(new Group(group.getId())));
			userGroupService.addUsersToGroups(Arrays.asList(user.getUserName()), Arrays.asList(group.getName()));

			profile.setUserName(user.getUserName());
			profile.setEmail(user.getMembership().getEmail());

			profileService.create(profile);

			SecurityContextHolder.getContext().setAuthentication(authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(user.getUserName(), password)));

			RequestContext.getCurrentInstance().addCallbackParam(COMMITTED_FLAG, true);
			
			return "success";
		} catch (Exception e) {
			RequestContext.getCurrentInstance().addCallbackParam(COMMITTED_FLAG, false);
			if (!handle(e))
				throw e;
		}
		return "failure";
	}

	private Application getApplication() throws Exception {
		final SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilderFactory.create().equal(pathFactory.create("name"), applicationName));

		List<Application> result = applicationService.find(searchOptions);
		if (result != null && result.size() == 1)
			return result.get(0);
		throw new IllegalStateException(String.format("No application with name '%s' was found.", applicationName));
	}

	private Group getGroup() throws Exception {
		final PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		final SearchOptions searchOptions = searchOptionsFactory.create(
				predicateBuilder.and(predicateBuilder.equal(pathFactory.create("name"), journalWebUsersGroupName),
						predicateBuilder.equal(pathFactory.create("application.name"), applicationName)));

		List<Group> result = groupService.find(searchOptions);
		if (result != null && result.size() == 1)
			return result.get(0);
		throw new IllegalStateException(
				String.format("No group with name '%s' has been found in the application with name '%s'.",
						journalWebUsersGroupName, applicationName));
	}

	public Profile getProfile() {
		return profile;
	}

}
