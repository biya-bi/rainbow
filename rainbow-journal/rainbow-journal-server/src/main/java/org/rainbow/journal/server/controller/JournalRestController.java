package org.rainbow.journal.server.controller;

import org.rainbow.criteria.PathFactory;
import org.rainbow.criteria.Predicate;
import org.rainbow.criteria.PredicateBuilderFactory;
import org.rainbow.criteria.SearchOptionsFactory;
import org.rainbow.journal.orm.entities.File;
import org.rainbow.journal.orm.entities.Journal;
import org.rainbow.journal.server.dto.FileDto;
import org.rainbow.journal.server.dto.JournalDto;
import org.rainbow.journal.server.dto.translation.DtoTranslator;
import org.rainbow.journal.server.search.JournalSearchParam;
import org.rainbow.journal.service.services.JournalService;
import org.rainbow.service.services.Service;
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
	private JournalService journalService;

	@Autowired
	@Qualifier("journalDtoTranslator")
	private DtoTranslator<Journal, JournalDto> journalDtoTranslator;

	@Autowired
	@Qualifier("fileDtoTranslator")
	private DtoTranslator<File, FileDto> fileDtoTranslator;

	@Autowired
	private PredicateBuilderFactory predicateBuilderFactory;

	@Autowired
	private PathFactory pathFactory;

	@Autowired
	private SearchOptionsFactory searchOptionsFactory;

	@Override
	protected Service<Journal> getService() {
		return journalService;
	}

	@Override
	protected DtoTranslator<Journal, JournalDto> getTranslator() {
		return journalDtoTranslator;
	}

	@Override
	protected Predicate getPredicate(JournalSearchParam searchParam) {
		if (searchParam.getName() != null) {
			return predicateBuilderFactory.create().contains(pathFactory.create("name"), searchParam.getName());
		}

		return null;
	}

	@RequestMapping(value = "/{id}/photo", method = RequestMethod.GET)
	public FileDto findPhoto(@PathVariable("id") long id) throws Exception {
		Journal journal = journalService.findById(id);
		if (journal != null && journal.getPhoto() != null)
			return fileDtoTranslator.toDto(journal.getPhoto());
		return null;
	}

	@Override
	protected SearchOptionsFactory getSearchOptionsFactory() {
		return searchOptionsFactory;
	}
}
