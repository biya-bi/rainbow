package org.rainbow.journal.faces.controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.rainbow.criteria.PathFactory;
import org.rainbow.criteria.PredicateBuilder;
import org.rainbow.criteria.PredicateBuilderFactory;
import org.rainbow.criteria.SearchOptionsFactory;
import org.rainbow.journal.faces.util.ProfileHelper;
import org.rainbow.journal.orm.entities.Journal;
import org.rainbow.journal.orm.entities.Publication;
import org.rainbow.journal.orm.entities.Subscription;
import org.rainbow.journal.service.services.JournalService;
import org.rainbow.journal.service.services.PublicationService;
import org.rainbow.journal.service.services.SubscriptionService;
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
	private JournalService journalService;

	@Autowired
	@Qualifier("subscriptionService")
	private SubscriptionService subscriptionService;

	@Autowired
	private ProfileHelper profileHelper;

	@Autowired
	@Qualifier("publicationService")
	private PublicationService publicationService;

	@Autowired
	private PathFactory pathFactory;

	@Autowired
	private PredicateBuilderFactory predicateBuilderFactory;

	@Autowired
	private SearchOptionsFactory searchOptionsFactory;

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

		this.publications = publicationService.find(searchOptionsFactory.create(
				predicateBuilderFactory.create().equal(pathFactory.create("journal.id"), selectedJournal.getId())));
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

		return subscriptionService.find(searchOptionsFactory.create(predicateBuilderFactory.create()
				.equal(pathFactory.create("subscriberProfile.userName"), getUserName())));

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

		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();
		List<Subscription> subscriptions = subscriptionService.find(searchOptionsFactory.create(
				predicateBuilder.and(predicateBuilder.equal(pathFactory.create("subscriberProfile.userName"), userName),
						predicateBuilder.equal(pathFactory.create("journal.id"), selectedJournal.getId()))));

		if (subscriptions != null && !subscriptions.isEmpty())
			subscriptionService.delete(subscriptions.get(0));
		else
			throw new IllegalStateException(
					String.format("No subscription to the journal '%s' for the user '%s' has been found.",
							selectedJournal.getName(), userName));
	}

}
