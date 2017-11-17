package org.rainbow.journal.server.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.rainbow.criteria.Expression;
import org.rainbow.criteria.PathFactory;
import org.rainbow.criteria.Predicate;
import org.rainbow.criteria.PredicateBuilder;
import org.rainbow.criteria.PredicateBuilderFactory;
import org.rainbow.criteria.SearchOptions;
import org.rainbow.criteria.SearchOptionsFactory;
import org.rainbow.journal.orm.entities.Journal;
import org.rainbow.journal.orm.entities.Profile;
import org.rainbow.journal.orm.entities.Subscription;
import org.rainbow.journal.server.dto.SubscriptionDto;
import org.rainbow.journal.server.dto.translation.DtoTranslator;
import org.rainbow.journal.server.search.SubscriptionSearchParam;
import org.rainbow.journal.service.services.ProfileService;
import org.rainbow.journal.service.services.SubscriptionService;
import org.rainbow.service.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
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
	private SubscriptionService subscriptionService;

	@Autowired
	@Qualifier("subscriptionDtoTranslator")
	private DtoTranslator<Subscription, SubscriptionDto> subscriptionDtoTranslator;

	@Autowired
	@Qualifier("profileService")
	private ProfileService profileService;

	@Autowired
	private SearchOptionsFactory searchOptionsFactory;

	@Autowired
	private PredicateBuilderFactory predicateBuilderFactory;

	@Autowired
	private PathFactory pathFactory;

	@Override
	protected Service<Subscription> getService() {
		return subscriptionService;
	}

	@Override
	protected DtoTranslator<Subscription, SubscriptionDto> getTranslator() {
		return subscriptionDtoTranslator;
	}

	@Override
	protected Predicate getPredicate(SubscriptionSearchParam searchParam) {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();

		List<Predicate> predicates = new ArrayList<>();

		if (searchParam.getJournalId() != null) {
			predicates.add(predicateBuilder.equal(pathFactory.create("journal.id"), searchParam.getJournalId()));
		}

		if (searchParam.getJournalName() != null) {
			predicates.add(predicateBuilder.contains(pathFactory.create("journal.name"), searchParam.getJournalName()));
		}

		if (searchParam.getSubscriptionDate() != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(searchParam.getSubscriptionDate());

			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
					0, 0, 0);
			Date lowerBound = calendar.getTime();

			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
					23, 59, 59);
			Date upperBound = calendar.getTime();

			Expression<String> exp = pathFactory.create("subscriptionDate");

			predicates.add(predicateBuilder.and(predicateBuilder.greaterThanOrEqualTo(exp, lowerBound),
					predicateBuilder.lessThanOrEqualTo(exp, upperBound)));

		}

		if (searchParam.getSubscriberProfileId() != null) {
			predicates.add(predicateBuilder.equal(pathFactory.create("subscriberProfile.id"),
					searchParam.getSubscriberProfileId()));
		}

		if (searchParam.getSubscriberUserName() != null) {
			predicates.add(predicateBuilder.equal(pathFactory.create("subscriberProfile.userName"),
					searchParam.getSubscriberUserName()));
		}

		if (!predicates.isEmpty()) {
			predicateBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
		}

		return null;
	}

	private Profile getCurrentProfile() throws Exception {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null)
			throw new IllegalStateException("A profile can only be searched for an authenticated user.");

		String userName = authentication.getName();

		List<Profile> profiles = profileService.find(searchOptionsFactory
				.create(predicateBuilderFactory.create().equal(pathFactory.create("userName"), userName)));

		if (profiles != null && !profiles.isEmpty())
			return profiles.get(0);
		throw new IllegalStateException(String.format("No profile for the user '%s' has been found.", userName));
	}

	private Authentication getAuthentication() {
		SecurityContext context = SecurityContextHolder.getContext();
		if (context != null) {
			return context.getAuthentication();
		}
		return null;
	}

	private String getUserName() {
		if (isAuthenticated()) {
			return getAuthentication().getName();
		}
		throw new IllegalStateException("The operation will be aborted because no authentication has been done.");
	}

	private boolean isAuthenticated() {
		final Authentication authentication = getAuthentication();
		if (authentication != null) {
			return authentication.isAuthenticated() &&
			// when Anonymous Authentication is enabled
					!(authentication instanceof AnonymousAuthenticationToken);
		}
		return false;
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

		final PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		final SearchOptions searchOptions = searchOptionsFactory.create(
				predicateBuilder.and(predicateBuilder.equal(pathFactory.create("subscriberProfile.userName"), userName),
						predicateBuilder.equal(pathFactory.create("journal.id"), journalId)));

		List<Subscription> subscriptions = subscriptionService.find(searchOptions);

		if (subscriptions != null && !subscriptions.isEmpty()) {
			subscriptionService.delete(subscriptions.get(0));
			return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
		} else
			throw new IllegalStateException(
					String.format("No subscription to the journal with ID '%d' for the user '%s' has been found.",
							journalId, userName));

	}

	@Override
	protected SearchOptionsFactory getSearchOptionsFactory() {
		return searchOptionsFactory;
	}
}
