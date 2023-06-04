package ru.graduatework.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.graduatework.controller.dto.TopicRequestDto;
import ru.graduatework.jdbc.PostgresOperatingDb;
import ru.graduatework.mapper.TopicDtoMapper;
import ru.graduatework.repository.ChapterTopicRepository;
import ru.graduatework.repository.CourseTopicRepository;
import ru.graduatework.repository.TopicRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TopicService {

    private final PostgresOperatingDb db;
    private final TopicRepository topicRepository;
    private final ChapterTopicRepository chapterTopicRepository;
    private final CourseTopicRepository courseTopicRepository;

    private final TopicDtoMapper topicDtoMapper;

    public void createByListFromChapter(List<TopicRequestDto> topics, UUID chapterId) {
        db.execute(ctx -> {
            var topicRecordList = topicDtoMapper.mapForCreate(topics);
            topicRecordList.forEach(topicRecord -> {
                topicRepository.create(ctx, topicRecord);
                chapterTopicRepository.create(ctx, topicRecord.getId(), chapterId);
            });
            return null;
        });
    }

    public void createByListFromCourse(List<TopicRequestDto> topics, UUID courseId) {
        db.execute(ctx -> {
            var topicRecordList = topicDtoMapper.mapForCreate(topics);
            topicRecordList.forEach(topicRecord -> {
                topicRepository.create(ctx, topicRecord);
                courseTopicRepository.create(ctx, topicRecord.getId(), courseId);
            });
            return null;
        });
    }

}
