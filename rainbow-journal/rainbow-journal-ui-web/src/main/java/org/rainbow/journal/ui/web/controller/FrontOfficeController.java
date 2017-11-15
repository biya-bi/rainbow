package org.rainbow.journal.ui.web.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.rainbow.core.persistence.SearchCriterion;
import org.rainbow.core.persistence.RelationalOperator;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.persistence.SingleValuedSearchCriterion;
import org.rainbow.journal.core.entities.Journal;
import org.rainbow.journal.core.entities.Publication;
import org.rainbow.journal.core.entities.Subscription;
import org.rainbow.journal.ui.web.utilities.ProfileHelper;
import org.rainbow.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Named
@SessionScoped
public class FrontOfficeController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8106256728118286225L;

	@Autowired
	@Qualifier("journalService")
	private Service<Journal, Long, SearchOptions> journalService;

	@Autowired
	@Qualifier("subscriptionService")
	private Service<Subscription, Long, SearchOptions> subscriptionService;

	@Autowired
	private ProfileHelper profileHelper;

	@Autowired
	@Qualifier("publicationService")
	private Service<Publication, Long, SearchOptions> publicationService;

	private Collection<Publication> publications = new ArrayList<>();

	private Journal selectedJournal;

	private Publication selectedPublication;

	public Collection<Publication> getPublications() {
		return publications;
	}

	public Journal getSelectedJournal() {
		return selectedJournal;
	}

	public void setSelectedJournal(Journal selectedJournal) {
		this.selectedJournal = selectedJournal;
	}

	public void getPublicationsForJournal() throws Exception {
		validateSelectedJournal();

		SearchOptions options = new SearchOptions();
		SingleValuedFilter<Long> filter = new SingleValuedFilter<>("journal.id", RelationalOperator.EQUAL,
				selectedJournal.getId());

		options.setFilters(Arrays.asList(new Filter<?>[] { filter }));

		this.publications = publicationService.find(options);
	}

	private void validateSelectedJournal() {
		if (selectedJournal == null)
			throw new IllegalStateException(
					"No journal is selected. Publications cannot be searched for a null journal.");
	}

	public Publication getSelectedPublication() {
		return selectedPublication;
	}

	public void setSelectedPublication(Publication selectedPublication) {
		this.selectedPublication = selectedPublication;
	}

	public Collection<Subscription> getSubscriptions() throws Exception {

		String userName = getUserName();
		SearchOptions options = new SearchOptions();
		StringSearchCriterion userNameSearchCriterion = new SingleValuedFilter<>("subscriberProfile.userName",
				RelationalOperator.EQUAL, userName);

		options.setFilters(Arrays.asList(new Filter<?>[] { userNameFilter }));

		return subscriptionService.find(options);

	}

	public void subscribe() throws Exception {
		validateSelectedJournal();

		String userName = getUserName();
		Subscription subscription = new Subscription();
		subscription.setJournal(selectedJournal);
		subscription.setSubscriberProfile(profileHelper.getCurrentProfile());
		subscription.setCreator(userName);
		subscription.setUpdater(userName);

		subscriptionService.create(subscription);
	}

	private String getUserName() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null)
			throw new IllegalStateException("The operation will be aborted because no authentication has been done.");

		return authentication.getName();
	}

	public void unsubscribe() throws Exception {
		validateSelectedJournal();

		String userName = getUserName();

		SearchOptions options = new SearchOptions();
		StringSearchCriterion userNameSearchCriterion = new SingleValuedFilter<>("subscriberProfile.userName",
				RelationalOperator.EQUAL, userName);
		SingleValuedFilter<Long> journalIdSearchCriterion = new SingleValuedFilter<>("journal.id", RelationalOperator.EQUAL,
				selectedJournal.getId());

		options.setFilters(Arrays.asList(new Filter<?>[] { userNameFilter, journalIdFilter }));

		List<Subscription> subscriptions = subscriptionService.find(options);

		if (subscriptions != null && !subscriptions.isEmpty())
			subscriptionService.delete(subscriptions.get(0));
		else
			throw new IllegalStateException(
					String.format("No subscription to the journal '%s' for the user '%s' has been found.",
							selectedJournal.getName(), userName));
	}

}
