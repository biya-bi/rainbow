package org.rainbow.journal.server.dto.translation;

import org.rainbow.journal.orm.entities.File;
import org.rainbow.journal.orm.entities.Journal;
import org.rainbow.journal.orm.entities.Profile;
import org.rainbow.journal.orm.entities.Publication;
import org.rainbow.journal.server.dto.PublicationDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("publicationDtoTranslator")
public class PublicationDtoTranslator implements DtoTranslator<Publication, PublicationDto> {

	@Override
	public PublicationDto toDto(Publication publication) {
		return new PublicationDto(publication.getDescription(),
				publication.getFile() != null ? publication.getFile().getId() : null, publication.getPublicationDate(),
				publication.getJournal().getId(), publication.getPublisherProfile().getId(), publication.getCreator(),
				publication.getUpdater(), publication.getCreationDate(), publication.getLastUpdateDate(),
				publication.getVersion(), publication.getId());
	}

	@Override
	public Publication fromDto(PublicationDto publicationDto) {
		return new Publication(publicationDto.getDescription(),
				publicationDto.getFileId() != null ? new File(publicationDto.getFileId()) : null,
				publicationDto.getPublicationDate(),
				publicationDto.getJournalId() != null ? new Journal(publicationDto.getJournalId()) : null,
				publicationDto.getPublisherProfileId() != null ? new Profile(publicationDto.getPublisherProfileId())
						: null,
				publicationDto.getCreator(), publicationDto.getUpdater(), publicationDto.getCreationDate(),
				publicationDto.getLastUpdateDate(), publicationDto.getVersion(), publicationDto.getId());
	}

}
