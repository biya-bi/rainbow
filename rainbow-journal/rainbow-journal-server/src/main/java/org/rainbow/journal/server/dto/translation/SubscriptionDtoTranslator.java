package org.rainbow.journal.server.dto.translation;

import org.rainbow.journal.core.entities.Journal;
import org.rainbow.journal.core.entities.Profile;
import org.rainbow.journal.core.entities.Subscription;
import org.rainbow.journal.server.dto.SubscriptionDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("subscriptionDtoTranslator")
public class SubscriptionDtoTranslator implements DtoTranslator<Subscription, SubscriptionDto> {

	@Override
	public SubscriptionDto toDto(Subscription subscription) {
		return new SubscriptionDto(subscription.getDescription(), subscription.getSubscriptionDate(),
				subscription.getJournal().getId(), subscription.getSubscriberProfile().getId(), subscription.getCreator(),
				subscription.getUpdater(), subscription.getCreationDate(), subscription.getLastUpdateDate(),
				subscription.getVersion(), subscription.getId());
	}

	@Override
	public Subscription fromDto(SubscriptionDto subscriptionDto) {
		return new Subscription(subscriptionDto.getDescription(),
				subscriptionDto.getSubscriptionDate(),
				subscriptionDto.getJournalId() != null ? new Journal(subscriptionDto.getJournalId()) : null,
				subscriptionDto.getSubscriberProfileId() != null ? new Profile(subscriptionDto.getSubscriberProfileId())
						: null,
				subscriptionDto.getCreator(), subscriptionDto.getUpdater(), subscriptionDto.getCreationDate(),
				subscriptionDto.getLastUpdateDate(), subscriptionDto.getVersion(), subscriptionDto.getId());
	}

}
