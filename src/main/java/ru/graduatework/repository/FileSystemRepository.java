package ru.graduatework.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import ru.graduatework.common.FlagFile;
import ru.graduatework.error.CommonException;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static ru.graduatework.error.Code.FILE_CREATION_ERROR;

@Repository
@Slf4j
public class FileSystemRepository {
    @Value("${file-system.storage.avatar}")
    private String dirAvatarUser;

    @Value("${file-system.storage.course-avatar}")
    private String dirAvatarCourse;

    @Value("${file-system.storage.networking-event-avatar}")
    private String dirAvatarNetworkingEvent;

    @Value("${file-system.storage.video}")
    private String dirVideoByCourse;

    public String save(byte[] content, UUID id, FlagFile flagFile) throws Exception {

        Path newFile;

        if (flagFile.equals(FlagFile.USER_AVATAR)) {
            newFile = Paths.get(dirAvatarUser + id.toString());
        } else if (flagFile.equals(FlagFile.EVENT_AVATAR)) {
            newFile = Paths.get(dirAvatarNetworkingEvent + id.toString());
        } else if (flagFile.equals(FlagFile.COURSE_AVATAR)) {
            newFile = Paths.get(dirAvatarCourse + id.toString());
        } else {
            newFile = Paths.get(dirVideoByCourse + id.toString());
        }

        Files.createDirectories(newFile.getParent());
        try {
            Files.write(newFile, content);
        } catch (Exception e) {
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
