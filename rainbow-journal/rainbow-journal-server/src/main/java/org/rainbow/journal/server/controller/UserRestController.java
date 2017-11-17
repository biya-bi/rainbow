package org.rainbow.journal.server.controller;

import java.util.Arrays;
import java.util.List;

import org.rainbow.criteria.PathFactory;
import org.rainbow.criteria.PredicateBuilder;
import org.rainbow.criteria.PredicateBuilderFactory;
import org.rainbow.criteria.SearchOptions;
import org.rainbow.criteria.SearchOptionsFactory;
import org.rainbow.journal.orm.entities.Profile;
import org.rainbow.journal.server.dto.SignupDto;
import org.rainbow.journal.service.services.ProfileService;
import org.rainbow.security.orm.entities.Application;
import org.rainbow.security.orm.entities.Group;
import org.rainbow.security.orm.entities.Membership;
import org.rainbow.security.orm.entities.User;
import org.rainbow.security.service.services.ApplicationService;
import org.rainbow.security.service.services.GroupService;
import org.rainbow.security.service.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

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
	@Qualifier("profileService")
	private ProfileService profileService;

	@Autowired
	@Qualifier("rainbowJournalApplicationName")
	private String applicationName;

	@Autowired
	@Qualifier("rainbowJournalWebUsersGroupName")
	private String journalWebUsersGroupName;

	@Autowired
	private PathFactory pathFactory;

	@Autowired
	private PredicateBuilderFactory predicateBuilderFactory;

	@Autowired
	private SearchOptionsFactory searchOptionsFactory;

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

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public void signup(@RequestBody SignupDto dto) throws Exception {
		User user = new User();

		user.setUserName(dto.getUserName());

		Membership membership = new Membership();
		membership.setPassword(dto.getPassword());
		membership.setEmail(dto.getEmail());
		membership.setPhone(dto.getPhone());

		user.setMembership(membership);

		Application application = getApplication();
		user.setApplication(application);

		Group group = getGroup();

		user.setGroups(Arrays.asList(group));

		userService.create(user);

		Profile profile = new Profile();
		profile.setUserName(dto.getUserName());
		profile.setEmail(dto.getEmail());
		profile.setLastName(dto.getLastName());
		profile.setFirstName(dto.getFirstName());
		profile.setBirthDate(dto.getBirthDate());

		profileService.create(profile);
	}

	private Profile getCurrentProfile() throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null)
			throw new IllegalStateException("A profile can only be searched for an authenticated user.");

		String userName = authentication.getName();

		final SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilderFactory.create().equal(pathFactory.create("userName"), userName));

		List<Profile> profiles = profileService.find(searchOptions);

		if (profiles != null && !profiles.isEmpty())
			return profiles.get(0);
		throw new IllegalStateException(String.format("No profile for the user '%s' has been found.", userName));
	}

	@RequestMapping(value = "/profileId", method = RequestMethod.GET)
	public Long findProfileId() throws Exception {
		return getCurrentProfile().getId();
	}
}
