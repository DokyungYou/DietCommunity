package com.example.dietcommunity.member.repository;

import com.example.dietcommunity.member.entity.QFollowing;
import com.example.dietcommunity.member.model.response.FollowingDto;
import com.example.dietcommunity.member.type.FollowingType;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class FollowingRepositoryCustomImpl implements FollowingRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;
  private final QFollowing qFollowing = QFollowing.following;


  @Override
  public Slice<FollowingDto> getFollowings(long memberId, FollowingType followingType, Long lastViewNum , Pageable pageable) {

    List<FollowingDto> followingDtos
        = jpaQueryFactory.select(Projections.constructor(FollowingDto.class,
        qFollowing.id,
        qFollowing.follower.id,
        qFollowing.follower.nickname
        ))
        .from(qFollowing)
        .where(createFollowingTypeCondition(memberId,followingType))

        .where(ltLastViewFollowingId(lastViewNum))
        .orderBy(qFollowing.id.desc())
        .limit(pageable.getPageSize() + 1) // 다음에 가져올 수 있는 데이터의 존재여부를 확인하기 위해 1개 더 가져온다.
        .fetch();


    // 실제로 가져온 데이터의 수가 기준사이즈보다 크다면 다음에 가져올 데이터가 있다는 뜻 (적어도 1개이상은 있음)
    boolean hasNext = false;
    if (followingDtos.size() > pageable.getPageSize()) {
      followingDtos.remove(pageable.getPageSize()); // 다음 데이터 존재여부 확인용으로 가져온 데이터는 제외해준다.
      hasNext = true;
    }

    return new SliceImpl<>(followingDtos, pageable, hasNext); // 다음 데이터의 존재여부를 같이 넣어서 반환
  }



  private BooleanExpression createFollowingTypeCondition(long memberId, FollowingType followingType) {

    if(followingType == FollowingType.FOLLOWING){  // member 가 FOLLOWING 을 한 상대들을 가져온다.
      return qFollowing.follower.id.eq(memberId);  // member가 follower(추종자) 인 데이터
    }

    if(followingType == FollowingType.FOLLOWER){  // member 를 FOLLOWING 한 FOLLOWER(추종자)
      return qFollowing.followee.id.eq(memberId); // member 가 팔로잉을 당하는 followee 인 데이터
    }
    throw new IllegalArgumentException("임시");
  }


  // 처음 시작할 땐 마지막으로 노출된 데이터가 없으니 조건없이 내림차순 한 것에서 10개만 가져오기
  private BooleanExpression ltLastViewFollowingId(Long lastViewDtoId) {
    return lastViewDtoId != null ? qFollowing.id.lt(lastViewDtoId) : null;
  }




}
