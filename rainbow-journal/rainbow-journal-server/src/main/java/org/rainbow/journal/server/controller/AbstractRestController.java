package org.rainbow.journal.server.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.rainbow.core.entities.Trackable;
import org.rainbow.core.persistence.Pageable;
import org.rainbow.core.persistence.SearchOptions;
import org.rainbow.core.service.Service;
import org.rainbow.journal.server.dto.translation.DtoTranslator;
import org.rainbow.journal.server.search.SearchParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

public abstract class AbstractRestController<TEntity extends Trackable<TKey>, TKey extends Serializable, TDto, TSearchParam extends SearchParam> {

	protected abstract Service<TEntity, TKey, SearchOptions> getService();

	protected abstract DtoTranslator<TEntity, TDto> getTranslator();

	protected void setSearchOptionsFilters(SearchOptions options) {
	}

	public static String v(Class<?> clz) {
		clz.getAnnotationsByType(Pageable.class);
		return null;
	}

	protected SearchOptions getSearchOptions(TSearchParam searchParam) {
		SearchOptions options = new SearchOptions();
		options.setPageIndex(searchParam.getPageIndex());
		options.setPageSize(searchParam.getPageSize());
		return options;
	}

	@RequestMapping(method = RequestMethod.GET)
	public List<TDto> find(@ModelAttribute TSearchParam searchParam) throws Exception {

		List<TDto> dtos = new ArrayList<>();
		List<TEntity> entities = getService().find(getSearchOptions(searchParam));
		for (TEntity entity : entities)
			dtos.add(getTranslator().toDto(entity));
		return dtos;
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<TKey> create(@RequestBody TDto dto) throws Exception {
		TEntity entity = getTranslator().fromDto(dto);

		getService().create(entity);

		return new ResponseEntity<TKey>(entity.getId(), HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Boolean> update(@PathVariable("id") TKey id, @RequestBody TDto dto) throws Exception {

		TEntity entity = getTranslator().fromDto(dto);
		entity.setId(id);

		getService().update(entity);

		return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.NO_CONTENT);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public TDto findById(@PathVariable("id") TKey id) throws Exception {
		return getTranslator().toDto(getService().findById(id));
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Boolean> delete(@PathVariable("id") TKey id) throws Exception {
		getService().delete(getService().findById(id));
		return new ResponseEntity<Boolean>(Boolean.TRUE, HttpStatus.OK);
	}
}
