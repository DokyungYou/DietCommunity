package com.example.dietcommunity.post.repository;

import com.example.dietcommunity.post.entity.Category;
import com.example.dietcommunity.post.entity.QPost;
import com.example.dietcommunity.post.model.PostDto;
import com.example.dietcommunity.post.type.CategoryType;
import com.example.dietcommunity.post.type.PostStatus;
import com.querydsl.core.types.Projections;
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
  public Page<PostDto> getPostListGeneral(Long categoryId, Pageable pageable) {

    // 카테고리 조건
    BooleanExpression categoryCondition = createCategoryCondition(categoryId);


    List<PostDto> postDtoList = jpaQueryFactory.select(Projections.constructor(PostDto.class,
            qPost.id.as("postId"),
            qPost.category,
            qPost.member.nickname.as("writerNickname"),
            qPost.title,
            qPost.totalHits,
            qPost.totalLikes,
            qPost.createdAt
        )).from(qPost)
        .where(categoryCondition)
        .where(qPost.postStatus.eq(PostStatus.NORMALITY))
        .orderBy(qPost.id.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    // 실행되지 않는 상태의 count 쿼리문
    JPAQuery<Long> totalCountQuery = jpaQueryFactory.select(qPost.count())
        .from(qPost)
        .where(categoryCondition)
        .where(qPost.postStatus.eq(PostStatus.NORMALITY));

    return PageableExecutionUtils.getPage(postDtoList, pageable, totalCountQuery::fetchOne);
  }

  private BooleanExpression createCategoryCondition(Long categoryId){
    if(categoryId == null){
      return qPost.category.categoryType.eq(CategoryType.GENERAL);
    }
    return qPost.category.id.eq(categoryId);
  }
}
