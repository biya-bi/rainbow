package org.rainbow.journal.server.controller;

import java.util.Arrays;

import org.rainbow.core.persistence.SearchCriterion;
import org.rainbow.core.persistence.RelationalOperator;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.persistence.SingleValuedSearchCriterion;
import org.rainbow.journal.core.entities.File;
import org.rainbow.journal.core.entities.Journal;
import org.rainbow.journal.server.dto.FileDto;
import org.rainbow.journal.server.dto.JournalDto;
import org.rainbow.journal.server.dto.translation.DtoTranslator;
import org.rainbow.journal.server.search.JournalSearchParam;
import org.rainbow.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/journals")
public class JournalRestController extends AbstractRestController<Journal, Long, JournalDto, JournalSearchParam> {

	@Autowired
	@Qualifier("journalService")
	private Service<Journal, Long, SearchOptions> journalService;

	@Autowired
	@Qualifier("journalDtoTranslator")
	private DtoTranslator<Journal, JournalDto> journalDtoTranslator;

	@Autowired
	@Qualifier("fileDtoTranslator")
	private DtoTranslator<File, FileDto> fileDtoTranslator;

	@Override
	protected Service<Journal, Long, SearchOptions> getService() {
		return journalService;
	}

	@Override
	protected DtoTranslator<Journal, JournalDto> getTranslator() {
		return journalDtoTranslator;
	}

	@Override
	protected SearchOptions getSearchOptions(JournalSearchParam searchParam) {
		SearchOptions options = super.getSearchOptions(searchParam);

		if (searchParam.getName() != null) {
			StringSearchCriterion nameSearchCriterion = new SingleValuedFilter<>("name", RelationalOperator.CONTAINS,
					searchParam.getName());

			options.setFilters(Arrays.asList(new Filter<?>[] { nameFilter }));
		}

		return options;
	}

	@RequestMapping(value = "/{id}/photo", method = RequestMethod.GET)
	public FileDto findPhoto(@PathVariable("id") long id) throws Exception {
		Journal journal = journalService.findById(id);
		if (journal != null && journal.getPhoto() != null)
			return fileDtoTranslator.toDto(journal.getPhoto());
		return null;
	}
}
