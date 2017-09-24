package org.rainbow.journal.server.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.rainbow.core.persistence.Filter;
import org.rainbow.core.persistence.RelationalOperator;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.persistence.SingleValuedFilter;
import org.rainbow.journal.core.entities.Journal;
import org.rainbow.journal.core.entities.Profile;
import org.rainbow.journal.core.entities.Subscription;
import org.rainbow.journal.server.dto.SubscriptionDto;
import org.rainbow.journal.server.dto.translation.DtoTranslator;
import org.rainbow.journal.server.search.SubscriptionSearchParam;
import org.rainbow.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionRestController
		extends AbstractRestController<Subscription, Long, SubscriptionDto, SubscriptionSearchParam> {

	@Autowired
	@Qualifier("subscriptionService")
	private Service<Subscription, Long, SearchOptions> subscriptionService;

	@Autowired
	@Qualifier("subscriptionDtoTranslator")
	private DtoTranslator<Subscription, SubscriptionDto> subscriptionDtoTranslator;

	@Autowired
	@Qualifier("profileService")
	private Service<Profile, Long, SearchOptions> profileService;

	@Override
	protected Service<Subscription, Long, SearchOptions> getService() {
		return subscriptionService;
	}

	@Override
	protected DtoTranslator<Subscription, SubscriptionDto> getTranslator() {
		return subscriptionDtoTranslator;
	}

	@Override
	protected SearchOptions getSearchOptions(SubscriptionSearchParam searchParam) {
		SearchOptions options = super.getSearchOptions(searchParam);

		List<Filter<?>> filters = new ArrayList<>();

		if (searchParam.getJournalId() != null) {
			SingleValuedFilter<Long> filter = new SingleValuedFilter<>("journal.id", RelationalOperator.EQUAL,
					searchParam.getJournalId());
			filters.add(filter);
		}

		if (searchParam.getJournalName() != null) {
			SingleValuedFilter<String> filter = new SingleValuedFilter<>("journal.name", RelationalOperator.CONTAINS,
					searchParam.getJournalName());
			filters.add(filter);
		}

		if (searchParam.getSubscriptionDate() != null) {
			SingleValuedFilter<Date> filter = new SingleValuedFilter<>("subscriptionDate", RelationalOperator.EQUAL,
					searchParam.getSubscriptionDate());
			filters.add(filter);
		}

		if (searchParam.getSubscriberProfileId() != null) {
			SingleValuedFilter<Long> filter = new SingleValuedFilter<>("subscriberProfile.id", RelationalOperator.EQUAL,
					searchParam.getSubscriberProfileId());
			filters.add(filter);
		}

		if (searchParam.getSubscriberUserName() != null) {
			SingleValuedFilter<String> filter = new SingleValuedFilter<>("subscriberProfile.userName",
					RelationalOperator.CONTAINS, searchParam.getSubscriberUserName());
			filters.add(filter);
		}

		if (!filters.isEmpty())
			options.setFilters(filters);

		return options;
	}

	private Profile getCurrentProfile() throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null)
			throw new IllegalStateException("A profile can only be searched for an authenticated user.");

		String userName = authentication.getName();

		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> filter = new SingleValuedFilter<>("userName", RelationalOperator.EQUAL, userName);

		options.setFilters(Arrays.asList(new Filter<?>[] { filter }));

		List<Profile> profiles = profileService.find(options);

		if (profiles != null && !profiles.isEmpty())
			return profiles.get(0);
		throw new IllegalStateException(String.format("No profile for the user '%s' has been found.", userName));
	}

	private String getUserName() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null)
			throw new IllegalStateException("The operation will be aborted because no authentication has been done.");

		return authentication.getName();
	}

	@RequestMapping(value = "/subscribe", method = RequestMethod.POST)
	public void subscribe(@RequestParam("journalId") Long journalId) throws Exception {
		if (journalId == null)
			throw new IllegalArgumentException("The journalId request parameter must be specified.");

		String userName = getUserName();
		Subscription subscription = new Subscription();
		subscription.setJournal(new Journal(journalId));
		subscription.setSubscriberProfile(getCurrentProfile());
		subscription.setCreator(userName);
		subscription.setUpdater(userName);

		subscriptionService.create(subscription);
	}

	@RequestMapping(value = "/unsubscribe", method = RequestMethod.DELETE)
	public ResponseEntity<Boolean> unsubscribe(@RequestParam("journalId") Long journalId) throws Exception {
		if (journalId == null)
			throw new IllegalArgumentException("The journalId request parameter must be specified.");
		
		String userName = getUserName();

		SearchOptions options = new SearchOptions();
		SingleValuedFilter<String> userNameFilter = new SingleValuedFilter<>("subscriberProfile.userName",
				RelationalOperator.EQUAL, userName);
		SingleValuedFilter<Long> journalIdFilter = new SingleValuedFilter<>("journal.id", RelationalOperator.EQUAL,
				journalId);

		options.setFilters(Arrays.asList(new Filter<?>[] { userNameFilter, journalIdFilter }));

		List<Subscription> subscriptions = subscriptionService.find(options);

		if (subscriptions != null && !subscriptions.isEmpty()) {
			subscriptionService.delete(subscriptions.get(0));
			return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
		} else
			throw new IllegalStateException(
					String.format("No subscription to the journal with ID '%d' for the user '%s' has been found.",
							journalId, userName));

	}
}
