package com.example.dietcommunity.member.repository;

import com.example.dietcommunity.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

  boolean existsByEmail(String email);
  boolean existsByNickname(String nickname);
  boolean existsByAccountId(String accountId);

  Optional<Member> findByEmail(String email);

  Optional<Member> findByAccountId(String accountId);

  Optional<Member> findByEmailAndAccountId(String email, String accountId);
}
