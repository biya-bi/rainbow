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
import org.rainbow.criteria.SearchOptionsFactory;
import org.rainbow.journal.orm.entities.File;
import org.rainbow.journal.orm.entities.Publication;
import org.rainbow.journal.server.dto.FileDto;
import org.rainbow.journal.server.dto.PublicationDto;
import org.rainbow.journal.server.dto.translation.DtoTranslator;
import org.rainbow.journal.server.search.PublicationSearchParam;
import org.rainbow.journal.service.services.PublicationService;
import org.rainbow.service.services.Service;
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
	private PublicationService publicationService;

	@Autowired
	@Qualifier("publicationDtoTranslator")
	private DtoTranslator<Publication, PublicationDto> publicationDtoTranslator;

	@Autowired
	@Qualifier("fileDtoTranslator")
	private DtoTranslator<File, FileDto> fileDtoTranslator;

	@Autowired
	private SearchOptionsFactory searchOptionsFactory;

	@Autowired
	private PredicateBuilderFactory predicateBuilderFactory;

	@Autowired
	private PathFactory pathFactory;

	@Override
	protected Service<Publication> getService() {
		return publicationService;
	}

	@Override
	protected DtoTranslator<Publication, PublicationDto> getTranslator() {
		return publicationDtoTranslator;
	}

	@Override
	protected Predicate getPredicate(PublicationSearchParam searchParam) {
		PredicateBuilder predicateBuilder = predicateBuilderFactory.create();

		List<Predicate> predicates = new ArrayList<>();

		if (searchParam.getJournalId() != null) {
			predicates.add(predicateBuilder.equal(pathFactory.create("journal.id"), searchParam.getJournalId()));
		}

		if (searchParam.getJournalName() != null) {
			predicates.add(predicateBuilder.contains(pathFactory.create("journal.name"), searchParam.getJournalName()));
		}

		if (searchParam.getPublicationDate() != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(searchParam.getPublicationDate());

			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
					0, 0, 0);
			Date lowerBound = calendar.getTime();

			calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
					23, 59, 59);
			Date upperBound = calendar.getTime();

			Expression<String> exp = pathFactory.create("publicationDate");

			predicates.add(predicateBuilder.and(predicateBuilder.greaterThanOrEqualTo(exp, lowerBound),
					predicateBuilder.lessThanOrEqualTo(exp, upperBound)));
		}

		if (searchParam.getPublisherProfileId() != null) {
			predicates.add(predicateBuilder.equal(pathFactory.create("publisherProfile.id"),
					searchParam.getPublisherProfileId()));
		}

		if (searchParam.getPublisherUserName() != null) {
			predicates.add(predicateBuilder.contains(pathFactory.create("publisherProfile.userName"),
					searchParam.getPublisherUserName()));
		}

		if (!predicates.isEmpty()) {
			predicateBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
		}

		return null;
	}

	@RequestMapping(value = "/{id}/photo", method = RequestMethod.GET)
	public FileDto findPhoto(@PathVariable("id") long id) throws Exception {
		Publication publication = publicationService.findById(id);
		if (publication != null && publication.getFile() != null)
			return fileDtoTranslator.toDto(publication.getFile());
		return null;
	}

	@Override
	protected SearchOptionsFactory getSearchOptionsFactory() {
		return searchOptionsFactory;
	}

}
