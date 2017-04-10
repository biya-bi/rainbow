package org.rainbow.journal.server.dto.translation;

import org.rainbow.journal.core.entities.File;
import org.rainbow.journal.server.dto.FileDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("fileDtoTranslator")
public class FileDtoTranslator implements DtoTranslator<File, FileDto> {

	@Override
	public FileDto toDto(File file) {
		return new FileDto(file.getFileName(), file.getFileContent(), file.getFileContentType(), file.getFileSize(),
				file.getCreator(), file.getUpdater(), file.getCreationDate(), file.getLastUpdateDate(),
				file.getVersion(), file.getId());
	}

	@Override
	public File fromDto(FileDto fileDto) {
		return new File(fileDto.getFileName(), fileDto.getFileContent(), fileDto.getFileContentType(), fileDto.getFileSize(),
				fileDto.getCreator(), fileDto.getUpdater(), fileDto.getCreationDate(), fileDto.getLastUpdateDate(),
				fileDto.getVersion(), fileDto.getId());
	}

}
