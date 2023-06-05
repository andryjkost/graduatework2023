package ru.graduatework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import ru.graduatework.common.FlagFile;
import ru.graduatework.common.Role;
import ru.graduatework.config.JwtService;
import ru.graduatework.controller.dto.CourseRequestDto;
import ru.graduatework.controller.dto.CourseResponseDto;
import ru.graduatework.controller.dto.CourseResponseShortDto;
import ru.graduatework.controller.dto.PaginatedResponseDto;
import ru.graduatework.jdbc.PostgresOperatingDb;
import ru.graduatework.mapper.CourseDtoMapper;
import ru.graduatework.repository.ArticleRepository;
import ru.graduatework.repository.AuthorCourseRepository;
import ru.graduatework.repository.CourseRepository;
import ru.graduatework.repository.FileSystemRepository;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {
    private final JwtService jwtService;
    private final AuthorService authorService;
    private final ChapterService chapterService;
    private final TopicService topicService;
    private final AuthorCourseRepository authorCourseRepository;
    private final UserService userService;

    private final PostgresOperatingDb db;

    private final FileSystemRepository fileSystemRepository;
    private final CourseRepository courseRepository;
    private final ArticleRepository articleRepository;

    private final CourseDtoMapper courseDtoMapper;

    public Mono<CourseResponseShortDto> create(String authToken, CourseRequestDto courseRequestDto) {
        var userId = jwtService.getUserIdByToken(authToken.substring(7));
        var authorId = authorService.getByUserId(userId).getId();
        return db.execAsync(ctx -> {
            var courseRecord = courseDtoMapper.mapForCreate(courseRequestDto);

            var course = courseDtoMapper.map(courseRepository.create(ctx, courseRecord));
            authorCourseRepository.createAuthorCourse(ctx, authorId, courseRecord.getId());
            if (courseRequestDto.getChapters() != null) {
                chapterService.createByListForChapter(courseRequestDto.getChapters(), courseRecord.getId());
            }
            if (courseRequestDto.getTopics() != null) {
                topicService.createByListFromCourse(courseRequestDto.getTopics(), courseRecord.getId());
            }
            return course;
        });
    }

    public Mono<Void> uploadAvatar(String authToken, MultipartFile image, UUID id) {
//        var jwt = authToken.substring(7);
//        var userId = UUID.fromString(jwtService.getUserIdFromJwt(jwt));
//        var user = db.execute(ctx -> userRepo.getById(ctx, userId));
        return db.execAsync(ctx -> {
            var course = courseRepository.getByIdShort(ctx, id);
            if (image == null) {
                if (course.getPathAvatar() != null) {
                    fileSystemRepository.delete(course.getPathAvatar());
                }
            }
            try {
                var newPath = fileSystemRepository.save(image.getBytes(), id, FlagFile.COURSE_AVATAR);
                courseRepository.addAvatar(ctx, newPath, id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    public Mono<Void> deletedAvatar(String authToken, UUID id) {
//        var jwt = authToken.substring(7);
//        var userId = UUID.fromString(jwtService.getUserIdFromJwt(jwt));

        return db.execAsync(ctx -> {
            var avatarPath = courseRepository.getByIdShort(ctx, id).getPathAvatar();
            courseRepository.addAvatar(ctx, null, id);
            fileSystemRepository.delete(avatarPath);

            return null;
        });
    }

    public Mono<PaginatedResponseDto<CourseResponseShortDto>> getPaginated(int offset, int limit, String authToken) {
        return db.execAsync(ctx -> {
            var tuple = courseRepository.getPaginated(ctx, offset, limit);
            var models = tuple.getT1();
            var result = models.stream().map(model -> {
                var dto = courseDtoMapper.map(model);
                dto.setArticleinfoShortForCourseResponseDtos(articleRepository.getByCourseId(ctx,dto.getId()));
                if (model.getPathToAvatar() != null) {
                    try {
                        dto.setImage(fileSystemRepository.findInFileSystem(model.getPathToAvatar()).getContentAsByteArray());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                return dto;
            }).toList();
            return PaginatedResponseDto.<CourseResponseShortDto>builder()
                    .result(result)
                    .totalCount(tuple.getT2())
                    .count(result.size())
                    .build();
        });

    }

    public Mono<CourseResponseDto> getById(String authToken, UUID id) {
        var jwt = authToken.substring(7);
        var userId = UUID.fromString(jwtService.getUserIdFromJwt(jwt));
        return db.execAsync(ctx -> {
            var user = userService.getById(userId);
            var model = courseRepository.getById(ctx, id, userId);
            var course = courseDtoMapper.map(model);
            course.setArticleinfoShortForCourseResponseDtos(articleRepository.getByCourseId(ctx, model.getId()));

            if (model.getPathToAvatar() != null) {
                try {
                    course.setImage(fileSystemRepository.findInFileSystem(model.getPathToAvatar()).getContentAsByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (Role.USER.equals(user.getRole()) && !course.getFlagPayment()) {
                return course;
            }

            course.setChapters(chapterService.getByCourseId(course.getId()));
            course.setTopics(topicService.getByCourseId(course.getId()));
            return course;
        });
    }
}
