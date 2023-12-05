package com.example.dietcommunity.post.repository;

import com.example.dietcommunity.common.exception.ErrorCode;
import com.example.dietcommunity.post.entity.Post;
import com.example.dietcommunity.post.entity.QPost;
import com.example.dietcommunity.post.type.CategoryType;
import com.example.dietcommunity.post.type.PostSortType;
import com.example.dietcommunity.post.type.PostStatus;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Slf4j
@RequiredArgsConstructor
@Repository
public class PostRepositoryCustomImpl implements PostRepositoryCustom{

  private final JPAQueryFactory jpaQueryFactory;
  private final QPost qPost = QPost.post;

  @Override
  public Page<Post> getPostListGeneral(Long categoryId, PostSortType postSortType, Pageable pageable) {

    // 카테고리 조건
    BooleanExpression categoryCondition = createCategoryCondition(categoryId);


    List<Post> postList = jpaQueryFactory.selectFrom(qPost)

        .where(categoryCondition)
        .where(qPost.postStatus.eq(PostStatus.NORMALITY))

        .orderBy(createOrderSpecifier(postSortType))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    // 실행되지 않는 상태의 count 쿼리문
    JPAQuery<Long> totalCountQuery = jpaQueryFactory.select(qPost.count())
        .from(qPost)
        .where(categoryCondition)
        .where(qPost.postStatus.eq(PostStatus.NORMALITY));

    return PageableExecutionUtils.getPage(postList, pageable, totalCountQuery::fetchOne);
  }

  private BooleanExpression createCategoryCondition(Long categoryId){
    if(categoryId == null){
      return qPost.category.categoryType.eq(CategoryType.GENERAL);
    }
    return qPost.category.id.eq(categoryId);
  }

  private OrderSpecifier<?> createOrderSpecifier(PostSortType postSortType){

    if (postSortType == PostSortType.LATEST || postSortType == null) {
      return qPost.id.desc();
    }

    if (postSortType == PostSortType.VIEWS) {
      return qPost.totalHits.desc();
    }

    if (postSortType == PostSortType.LIKES) {
      return qPost.totalLikes.desc();
    }

    log.error("유효하지 않은 PostSortType: {}", postSortType.name());
    throw new IllegalArgumentException(ErrorCode.ILLEGAL_ARGUMENT_EXCEPTION.getDescription());
  }

}
