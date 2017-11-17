package org.rainbow.journal.faces.util;

import java.util.List;

import org.rainbow.criteria.PathFactory;
import org.rainbow.criteria.PredicateBuilderFactory;
import org.rainbow.criteria.SearchOptions;
import org.rainbow.criteria.SearchOptionsFactory;
import org.rainbow.journal.orm.entities.Profile;
import org.rainbow.journal.service.services.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ProfileHelper {

	@Autowired
	@Qualifier("profileService")
	private ProfileService profileService;

	@Autowired
	private PathFactory pathFactory;

	@Autowired
	private PredicateBuilderFactory predicateBuilderFactory;

	@Autowired
	private SearchOptionsFactory searchOptionsFactory;

	public Profile getCurrentProfile() throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null)
			throw new IllegalStateException("A profile can only be searched after for an authenticated.");

		String userName = authentication.getName();

		final SearchOptions searchOptions = searchOptionsFactory
				.create(predicateBuilderFactory.create().equal(pathFactory.create("userName"), userName));

		List<Profile> profiles = profileService.find(searchOptions);

		if (profiles != null && !profiles.isEmpty())
			return profiles.get(0);
		throw new IllegalStateException(String.format("No profile for the user '%s' has been found.", userName));
	}
}
