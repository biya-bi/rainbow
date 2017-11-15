package org.rainbow.journal.server.controller;

import java.util.Arrays;
import java.util.List;

import org.rainbow.core.persistence.SearchCriterion;
import org.rainbow.core.persistence.RelationalOperator;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.persistence.SingleValuedSearchCriterion;
import org.rainbow.journal.core.entities.Profile;
import org.rainbow.journal.server.dto.SignupDto;
import org.rainbow.security.core.entities.Application;
import org.rainbow.security.core.entities.Group;
import org.rainbow.security.core.entities.Membership;
import org.rainbow.security.core.entities.User;
import org.rainbow.security.core.service.UserGroupService;
import org.rainbow.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
	private Service<User, Long, SearchOptions> userService;

	@Autowired
	@Qualifier("applicationService")
	private Service<Application, Long, SearchOptions> applicationService;

	@Autowired
	@Qualifier("groupService")
	private Service<Group, Long, SearchOptions> groupService;

	@Autowired
	@Qualifier("profileService")
	private Service<Profile, Long, SearchOptions> profileService;

	@Autowired
	@Qualifier("userGroupService")
	private UserGroupService userGroupService;

	@Value("${application.name}")
	private String applicationName;

	@Value("${default.journal.web.user.group.name}")
	private String defaultJournalWebUserGroupName;

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

	@RequestMapping(value = "/signup", method = RequestMethod.POST)
	public void signup(@RequestBody SignupDto dto) throws Exception {
		User user = new User();

		user.setUserName(dto.getUserName());
		user.setCreator(dto.getUserName());
		user.setUpdater(dto.getUserName());

		Membership membership = new Membership();
		membership.setPassword(dto.getPassword());
		membership.setEmail(dto.getEmail());
		membership.setPasswordQuestion(dto.getPasswordQuestion());
		membership.setPasswordQuestionAnswer(dto.getPasswordQuestionAnswer());
		membership.setPhone(dto.getPhone());

		user.setMembership(membership);

		Application application = getApplication();
		user.setApplication(application);

		Group group = getGroup();

		userService.create(user);

		userGroupService.addUsersToGroups(Arrays.asList(new Long[] { user.getId() }),
				Arrays.asList(new Long[] { group.getId() }), application.getId());

		Profile profile = new Profile();
		profile.setUserName(dto.getUserName());
		profile.setEmail(dto.getEmail());
		profile.setCreator(dto.getUserName());
		profile.setUpdater(dto.getUserName());
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

		SearchOptions options = new SearchOptions();
		StringSearchCriterion filter = new SingleValuedFilter<>("userName", RelationalOperator.EQUAL, userName);

		options.setFilters(Arrays.asList(new Filter<?>[] { filter }));

		List<Profile> profiles = profileService.find(options);

		if (profiles != null && !profiles.isEmpty())
			return profiles.get(0);
		throw new IllegalStateException(String.format("No profile for the user '%s' has been found.", userName));
	}

	@RequestMapping(value = "/profileId", method = RequestMethod.GET)
	public Long findProfileId() throws Exception {
		return getCurrentProfile().getId();
	}
}
