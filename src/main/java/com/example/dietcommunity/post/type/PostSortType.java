package com.example.dietcommunity.post.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@Getter
@AllArgsConstructor
public enum PostSortType {

  LATEST("createdAt", Direction.DESC),
  VIEWS("totalHits", Direction.DESC),
  LIKES("totalLikes",Direction.DESC)

//  CHALLENGE_IMMINENT("challengeStartDate",Direction.ASC) // ABLE_APPLY 외에는 이걸 사용하기는 애매해서 사용하지 않는걸로
;
  private final String sortCriteria;
  private final Sort.Direction sortOrder;
}
