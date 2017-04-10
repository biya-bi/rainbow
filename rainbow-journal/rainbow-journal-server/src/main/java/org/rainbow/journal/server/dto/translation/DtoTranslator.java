package org.rainbow.journal.server.dto.translation;

public interface DtoTranslator<TEntity, TDto> {
	TDto toDto(TEntity entity);

	TEntity fromDto(TDto dto);
}
