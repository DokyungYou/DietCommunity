package com.example.dietcommunity.post.repository;

import com.example.dietcommunity.post.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> ,ChallengeRepositoryCustom{

}
