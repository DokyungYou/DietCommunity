package com.example.dietcommunity.post.repository;

import com.example.dietcommunity.common.exception.ErrorCode;
import com.example.dietcommunity.post.entity.QChallenge;
import com.example.dietcommunity.post.model.ChallengeDto;
import com.example.dietcommunity.post.model.PostDto;
import com.example.dietcommunity.post.type.CategoryType;
import com.example.dietcommunity.post.type.ChallengeQueryFilter;
import com.example.dietcommunity.post.type.PostSortType;
import com.example.dietcommunity.post.type.PostStatus;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

@Slf4j
@RequiredArgsConstructor
public class ChallengeRepositoryCustomImpl implements ChallengeRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;
  private final QChallenge qChallenge = QChallenge.challenge;

  @Override
  public Page<ChallengeDto> getChallengeList(Long categoryId, ChallengeQueryFilter status, PostSortType postSortType,
      Pageable pageable) {

    BooleanExpression categoryCondition = createCategoryCondition(categoryId);

    List<ChallengeDto> challengeDtoList
        = jpaQueryFactory.select(Projections.constructor(ChallengeDto.class,
                qChallenge.id,
                qChallenge.challengeStartDate,
                qChallenge.challengeEndDate,
                qChallenge.limitApplicantsNumber,
                qChallenge.participateChannelId,
                qChallenge.isFilled,

                Projections.fields(PostDto.class,
                    qChallenge.post.id.as("postId"),
                    qChallenge.post.member.nickname.as("writerNickname"),
                    qChallenge.post.title,
                    qChallenge.post.totalHits,
                    qChallenge.post.totalLikes,
                    qChallenge.post.createdAt
                )
            )
        )
        .from(qChallenge)

        .where(categoryCondition)
        .where(createChallengeStatusCondition(status))

        .where(qChallenge.post.postStatus.eq(PostStatus.NORMALITY))

        .orderBy(createOrderSpecifier(postSortType))

        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    // 실행되지 않는 상태의 count 쿼리문
    JPAQuery<Long> totalCountQuery = jpaQueryFactory.select(qChallenge.count())
        .from(qChallenge)
        .where(categoryCondition)
        .where(qChallenge.post.postStatus.eq(PostStatus.NORMALITY));

    return PageableExecutionUtils.getPage(challengeDtoList, pageable, totalCountQuery::fetchOne);
  }


  private BooleanExpression createCategoryCondition(Long categoryId) {
    if (categoryId == null) {
      return qChallenge.post.category.categoryType.eq(CategoryType.CHALLENGE);
    }

    return qChallenge.post.category.id.eq(categoryId);
  }

  private BooleanExpression createChallengeStatusCondition(ChallengeQueryFilter filter) {

    if (filter == ChallengeQueryFilter.WHOLE || filter == null) {
      return null;
    }

    // 시작하지않았으면서 자리가 남아있어야함
    if (filter == ChallengeQueryFilter.ABLE_APPLY) {
      return qChallenge.challengeStartDate.after(LocalDate.now()).and(qChallenge.isFilled.isFalse());
    }

    log.error("유효하지 않은 ChallengeQueryFilter: {}", filter.name());
    throw new IllegalArgumentException(ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION.getDescription());
  }

  private OrderSpecifier<?> createOrderSpecifier(PostSortType postSortType) {

    if (postSortType == PostSortType.LATEST || postSortType == null) {
      return qChallenge.post.id.desc();
    }

    if (postSortType == PostSortType.VIEWS) {
      return qChallenge.post.totalHits.desc();
    }

    if (postSortType == PostSortType.LIKES) {
      return qChallenge.post.totalLikes.desc();
    }

    log.error("유효하지 않은 PostSortType: {}", postSortType.name());
    throw new IllegalArgumentException(ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION.getDescription());
  }


}
