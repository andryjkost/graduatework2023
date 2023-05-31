package ru.graduatework.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.graduatework.controller.dto.ArticleShortResponseDto;
import ru.graduatework.controller.dto.PaginatedResponseDto;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jooq.tables.records.ArticleRecord;
import ru.graduatework.mapper.ArticleDtoMapper;

import static ru.graduatework.jooq.Tables.ARTICLE;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ArticleRepository {

    private final ArticleDtoMapper articleDtoMapper;

    public ArticleRecord createArticle(PostgresOperatingContext ctx, ArticleRecord articleRecord) {
        return ctx.dsl().insertInto(ARTICLE).set(articleRecord).returning().fetchOne();
    }

    public ArticleRecord getById(PostgresOperatingContext ctx, Long id) {
        return ctx.dsl().selectFrom(ARTICLE).where(ARTICLE.ID.eq(id)).fetchOneInto(ArticleRecord.class);
    }

    public PaginatedResponseDto<ArticleShortResponseDto> getPaginatedShortArticle(PostgresOperatingContext ctx, int offset, int limit) {
        var selectQuery = ctx.dsl().selectFrom(ARTICLE).orderBy(ARTICLE.TIME_OF_CREATION_OR_MODIFICATION.desc());

        var totalCount = ctx.dsl()
                .selectCount()
                .from(selectQuery)
                .fetchOneInto(Integer.class);
        var articleList = selectQuery
                .offset(offset)
                .limit(limit > 0 ? limit : null)
                .fetchInto(ArticleRecord.class);

        var result = articleDtoMapper.mapShort(articleList);

        return PaginatedResponseDto.<ArticleShortResponseDto>builder()
                .count(result.size())
                .result(result)
                .totalCount(totalCount)
                .build();
    }
}
