package org.rainbow.journal.ui.web.utilities;

import java.util.Arrays;
import java.util.List;

import org.rainbow.core.persistence.SearchCriterion;
import org.rainbow.core.persistence.RelationalOperator;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.persistence.SingleValuedSearchCriterion;
import org.rainbow.journal.core.entities.Profile;
import org.rainbow.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class ProfileHelper {
	@Autowired
	@Qualifier("profileService")
	private Service<Profile, Long, SearchOptions> profileService;

	public Profile getCurrentProfile() throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null)
			throw new IllegalStateException("A profile can only be searched after for an authenticated.");

		String userName = authentication.getName();

		SearchOptions options = new SearchOptions();
		StringSearchCriterion filter = new SingleValuedFilter<>("userName", RelationalOperator.EQUAL, userName);

		options.setFilters(Arrays.asList(new Filter<?>[] { filter }));

		List<Profile> profiles = profileService.find(options);

		if (profiles != null && !profiles.isEmpty())
			return profiles.get(0);
		throw new IllegalStateException(String.format("No profile for the user '%s' has been found.", userName));
	}
}
