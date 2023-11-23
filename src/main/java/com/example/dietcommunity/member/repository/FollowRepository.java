package com.example.dietcommunity.member.repository;

import com.example.dietcommunity.member.entity.Follow;
import java.util.Optional;
import javax.swing.text.html.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

//  Optional<Follow> findByMember_MemberIdAndFollowing_MemberId(long followerId, long followingId);

  boolean existsByMember_MemberIdAndFollowing_MemberId(long followerId, long followingId);

  void deleteByMember_MemberIdAndFollowing_MemberId(long memberId, long followingId);
}
