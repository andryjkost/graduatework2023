package ru.graduatework.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import ru.graduatework.error.CommonException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static ru.graduatework.error.Code.FILE_CREATION_ERROR;

@Repository
@Slf4j
public class FileSystemRepository {
    @Value("${file-system.storage.avatar}")
    private String dirAvatar;

    public String save(byte[] content, Long userId) throws Exception {
        Path newFile = Paths.get(dirAvatar + userId);
        Files.createDirectories(newFile.getParent());
        try {
            Files.write(newFile, content);
        }
       catch (Exception e){
            log.error("File creation error");
           throw CommonException.builder().code(FILE_CREATION_ERROR).userMessage("Ошибка создания файл").techMessage("File creation error").httpStatus(HttpStatus.BAD_REQUEST).build();
       }
        return newFile.toAbsolutePath()
                .toString();
    }

    public void delete(String location) {
        try {
            File file = new File(location);
            if (file.delete()) {
                log.info("File with path {} deleted", location);
            } else {
                log.info("File with path {} not found", location);
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public FileSystemResource findInFileSystem(String location) {
        try {
            return new FileSystemResource(Paths.get(location));
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}
