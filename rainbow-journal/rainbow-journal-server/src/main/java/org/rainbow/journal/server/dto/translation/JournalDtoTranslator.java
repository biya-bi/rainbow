package org.rainbow.journal.server.dto.translation;

import org.rainbow.journal.core.entities.File;
import org.rainbow.journal.core.entities.Journal;
import org.rainbow.journal.core.entities.Profile;
import org.rainbow.journal.server.dto.JournalDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("journalDtoTranslator")
public class JournalDtoTranslator implements DtoTranslator<Journal, JournalDto> {

	@Override
	public JournalDto toDto(Journal journal) {
		return new JournalDto(journal.getName(), journal.getDescription(),
				journal.getPhoto() != null ? journal.getPhoto().getId() : null, journal.getTag(), journal.isActive(),
				journal.getOwnerProfile() != null ? journal.getOwnerProfile().getId() : null, journal.getCreator(),
				journal.getUpdater(), journal.getCreationDate(), journal.getLastUpdateDate(), journal.getVersion(),
				journal.getId());
	}

	@Override
	public Journal fromDto(JournalDto journalDto) {
		return new Journal(journalDto.getName(), journalDto.getDescription(),
				journalDto.getPhotoId() != null ? new File(journalDto.getPhotoId()) : null, journalDto.getTag(),
				journalDto.isActive(),
				journalDto.getOwnerProfileId() != null ? new Profile(journalDto.getOwnerProfileId()) : null,
				journalDto.getCreator(), journalDto.getUpdater(), journalDto.getCreationDate(),
				journalDto.getLastUpdateDate(), journalDto.getVersion(), journalDto.getId());
	}

}
