package com.example.dietcommunity.member.repository;

import com.example.dietcommunity.member.entity.Following;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowingRepository extends JpaRepository<Following, Long> , FollowingRepositoryCustom{

//  Optional<Follow> findByMember_MemberIdAndFollowing_MemberId(long followerId, long followingId);

  boolean existsByFollower_IdAndFollowee_Id(long followerId, long followeeId);

  void deleteByFollower_IdAndFollowee_Id(long memberId, long followingId);
}
