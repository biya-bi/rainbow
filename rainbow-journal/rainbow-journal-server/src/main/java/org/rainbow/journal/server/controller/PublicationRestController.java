package org.rainbow.journal.server.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.rainbow.core.persistence.SearchCriterion;
import org.rainbow.core.persistence.RelationalOperator;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.persistence.SingleValuedSearchCriterion;
import org.rainbow.journal.core.entities.File;
import org.rainbow.journal.core.entities.Publication;
import org.rainbow.journal.server.dto.FileDto;
import org.rainbow.journal.server.dto.PublicationDto;
import org.rainbow.journal.server.dto.translation.DtoTranslator;
import org.rainbow.journal.server.search.PublicationSearchParam;
import org.rainbow.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/publications")
public class PublicationRestController
		extends AbstractRestController<Publication, Long, PublicationDto, PublicationSearchParam> {

	@Autowired
	@Qualifier("publicationService")
	private Service<Publication, Long, SearchOptions> publicationService;

	@Autowired
	@Qualifier("publicationDtoTranslator")
	private DtoTranslator<Publication, PublicationDto> publicationDtoTranslator;

	@Autowired
	@Qualifier("fileDtoTranslator")
	private DtoTranslator<File, FileDto> fileDtoTranslator;

	@Override
	protected Service<Publication, Long, SearchOptions> getService() {
		return publicationService;
	}

	@Override
	protected DtoTranslator<Publication, PublicationDto> getTranslator() {
		return publicationDtoTranslator;
	}

	@Override
	protected SearchOptions getSearchOptions(PublicationSearchParam searchParam) {
		SearchOptions options = super.getSearchOptions(searchParam);

		List<Filter<?>> filters = new ArrayList<>();

		if (searchParam.getJournalId() != null) {
			SingleValuedFilter<Long> filter = new SingleValuedFilter<>("journal.id", RelationalOperator.EQUAL,
					searchParam.getJournalId());
			filters.add(filter);
		}

		if (searchParam.getJournalName() != null) {
			StringSearchCriterion filter = new SingleValuedFilter<>("journal.name", RelationalOperator.CONTAINS,
					searchParam.getJournalName());
			filters.add(filter);
		}

		if (searchParam.getPublicationDate() != null) {
			SingleValuedFilter<Date> filter = new SingleValuedFilter<>("publicationDate", RelationalOperator.EQUAL,
					searchParam.getPublicationDate());
			filters.add(filter);
		}

		if (searchParam.getPublisherProfileId() != null) {
			SingleValuedFilter<Long> filter = new SingleValuedFilter<>("publisherProfile.id", RelationalOperator.EQUAL,
					searchParam.getPublisherProfileId());
			filters.add(filter);
		}

		if (searchParam.getPublisherUserName() != null) {
			StringSearchCriterion filter = new SingleValuedFilter<>("publisherProfile.userName", RelationalOperator.CONTAINS,
					searchParam.getPublisherUserName());
			filters.add(filter);
		}

		if (!filters.isEmpty())
			options.setFilters(filters);

		return options;
	}

	@RequestMapping(value = "/{id}/photo", method = RequestMethod.GET)
	public FileDto findPhoto(@PathVariable("id") long id) throws Exception {
		Publication publication = publicationService.findById(id);
		if (publication != null && publication.getFile() != null)
			return fileDtoTranslator.toDto(publication.getFile());
		return null;
	}

}
