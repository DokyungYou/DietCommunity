package com.example.dietcommunity.member.repository;

import com.example.dietcommunity.member.entity.QFollowing;
import com.example.dietcommunity.member.model.response.FollowingDto;
import com.example.dietcommunity.member.type.FollowingType;
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
public class FollowingRepositoryCustomImpl implements FollowingRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;
  private final QFollowing qFollowing = QFollowing.following;


  @Override
  public Page<FollowingDto> getFollowings(long memberId, FollowingType followingType, Pageable pageable) {

    List<FollowingDto> followingDtos = jpaQueryFactory.select(Projections.constructor(FollowingDto.class,
        qFollowing.id,
        qFollowing.follower.id,
        qFollowing.follower.nickname
        ))
        .from(qFollowing)
        .where(createFollowingTypeCondition(memberId, followingType))
        .offset(pageable.getOffset())  // AbstractPageRequest 의 public long getOffset() { return (long) page * (long) size;}

        .orderBy(qFollowing.id.desc())
        .limit(pageable.getPageSize())
        .fetch();


    // 실행되지 않는 상태의 count 쿼리문
    JPAQuery<Long> totalCountQuery = jpaQueryFactory.select(qFollowing.count())
        .from(qFollowing)
        .where(createFollowingTypeCondition(memberId, followingType));


    return PageableExecutionUtils.getPage(followingDtos, pageable, totalCountQuery::fetchOne);

     /*  < 화면에 노출 될 페이지를 계산하기위한 totalCount 쿼리가 필요하지 않은 경우 >
      1. 페이지가 없거나
      2. 첫번째 페이지이면서 콘텐츠사이즈가 한 페이지의 사이즈보다 작을때  -> content.size() 가 totalCount
      3. 마지막페이지일때 (offset 건너뛰고 가져온 데이터의 개수가 한 페이지의 사이즈보다 작을 때 )  ->  pageable.getOffset() + content.size() 가 totalCount

      이 외의 경우에만  totalCountQuery 가 실행됨
     */


  }



  private BooleanExpression createFollowingTypeCondition(long memberId, FollowingType followingType) {

    if (followingType == null) {
      throw new NullPointerException("FollowingType 은 null 일 수 없습니다.");
    }


    if(followingType == FollowingType.FOLLOWING){  // member 가 FOLLOWING 을 한 상대들을 가져온다.
      return qFollowing.follower.id.eq(memberId);  // member가 follower(추종자) 인 데이터
    }
    if(followingType == FollowingType.FOLLOWER){  // member 를 FOLLOWING 한 FOLLOWER(추종자)
      return qFollowing.followee.id.eq(memberId); // member 가 팔로잉을 당하는 followee 인 데이터
    }


    throw new IllegalArgumentException("팔로잉, 팔로워 조회 시에 사용되는 FollowingType 이 아닙니다. : " + followingType);
  }



}
