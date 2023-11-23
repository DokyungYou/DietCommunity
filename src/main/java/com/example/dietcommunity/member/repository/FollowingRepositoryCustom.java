package com.example.dietcommunity.member.repository;

import com.example.dietcommunity.member.model.response.FollowingDto;
import com.example.dietcommunity.member.type.FollowingType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface FollowingRepositoryCustom {

  Slice<FollowingDto> getFollowings(long memberId, FollowingType followingType ,Long lastViewNum, Pageable pageable);

}
