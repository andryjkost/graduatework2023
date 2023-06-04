package ru.graduatework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.graduatework.controller.dto.ChapterRequestDto;
import ru.graduatework.controller.dto.ChapterResponseDto;
import ru.graduatework.jdbc.PostgresOperatingDb;
import ru.graduatework.mapper.ChapterDtoMapper;
import ru.graduatework.repository.ChapterRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChapterService {

    private final TopicService topicService;

    private final PostgresOperatingDb db;
    private final ChapterRepository chapterRepository;

    private final ChapterDtoMapper chapterDtoMapper;

    public void createByListForChapter(List<ChapterRequestDto> chapters, UUID courseId) {
        db.execute(ctx -> {
            chapters.forEach(chapter -> {
                var chapterRecord = chapterDtoMapper.mapForCreate(chapter, courseId);
                chapterRepository.create(ctx, chapterRecord);
                topicService.createByListFromChapter(chapter.getTopics(), chapterRecord.getId());
            });
            return null;
        });
    }

    public List<ChapterResponseDto> getByCourseId(UUID courseId) {
        return db.execute(ctx -> chapterDtoMapper.map(chapterRepository.getByCourseId(ctx, courseId))
                .stream().peek(record -> record.setTopics(topicService.getByChapterId(record.getId()))).toList());
    }
}
