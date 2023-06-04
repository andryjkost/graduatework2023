package ru.graduatework.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import ru.graduatework.common.Utils;
import ru.graduatework.jdbc.PostgresOperatingContext;
import ru.graduatework.jooq.tables.records.ArticleRecord;
import ru.graduatework.mapper.ArticleDtoMapper;
import ru.graduatework.model.ArticleWithAuthorModel;
import ru.graduatework.model.AuthorShortModel;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static ru.graduatework.jooq.Tables.*;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ArticleRepository {

    public boolean update(PostgresOperatingContext ctx, ArticleRecord articleRecord, UUID articleId) {
        return ctx.dsl().update(ARTICLE)
                .set(articleRecord)
                .where(ARTICLE.ID.eq(articleId)).execute() == 1;
    }

    public ArticleRecord createArticle(PostgresOperatingContext ctx, ArticleRecord articleRecord) {
        return ctx.dsl().insertInto(ARTICLE).set(articleRecord).returning().fetchOne();
    }

    public ArticleWithAuthorModel getById(PostgresOperatingContext ctx, UUID id) {
        return ctx.dsl()
                .select(ARTICLE.asterisk(), AUTHOR.ID, AUTHOR.LAST_NAME, AUTHOR.FIRST_NAME)
                .from(ARTICLE
                        .leftJoin(AUTHOR_ARTICLE).on(ARTICLE.ID.eq(AUTHOR_ARTICLE.ARTICLE_ID))
                        .leftJoin(AUTHOR).on(AUTHOR_ARTICLE.AUTHOR_ID.eq(AUTHOR.ID)))
                .where(ARTICLE.ID.eq(id))
                .fetchOne(record -> ArticleWithAuthorModel.builder()
                        .id((UUID) record.get(0))
                        .title((String) record.getValue(1))
                        .textArticle((String) record.getValue(2))
                        .timeModification((OffsetDateTime) record.get(3))
                        .authorShortModel(AuthorShortModel.builder()
                                .id((UUID) record.get(5))
                                .firstLastName(Utils.getFullName((String) record.get(6), (String) record.get(7)))
                                .build())
                        .build());
    }

    public Tuple2<Integer, List<ArticleWithAuthorModel>> getPaginatedShortArticle(PostgresOperatingContext ctx, int offset, int limit) {
        var selectQuery = ctx.dsl()
                .select(ARTICLE.ID, ARTICLE.TITLE,
                        AUTHOR.ID, AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
                .from(ARTICLE
                        .leftJoin(AUTHOR_ARTICLE).on(ARTICLE.ID.eq(AUTHOR_ARTICLE.ARTICLE_ID))
                        .leftJoin(AUTHOR).on(AUTHOR.ID.eq(AUTHOR_ARTICLE.AUTHOR_ID)))
                .orderBy(ARTICLE.TIME_OF_CREATION_OR_MODIFICATION.desc());

        var totalCount = ctx.dsl()
                .selectCount()
                .from(selectQuery)
                .fetchOneInto(Integer.class);

        var result = selectQuery
                .offset(offset)
                .limit(limit > 0 ? limit : null)
                .fetch().map(record -> ArticleWithAuthorModel.builder()
                        .id((UUID) record.get(0))
                        .title((String) record.get(1))
                        .authorShortModel(AuthorShortModel.builder()
                                .id((UUID) record.get(2))
                                .firstLastName(Utils.getFullName((String) record.get(4), (String) record.get(3)))
                                .build())
                        .build());

        return Tuples.of(totalCount, result);
    }
}
