package com.example.dietcommunity.member.repository;

import com.example.dietcommunity.member.model.response.FollowingDto;
import com.example.dietcommunity.member.type.FollowingType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FollowingRepositoryCustom {

  Page<FollowingDto> getFollowings(long memberId, FollowingType followingType , Pageable pageable);

}
