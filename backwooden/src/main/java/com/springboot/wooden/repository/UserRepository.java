package com.springboot.wooden.repository;

import com.springboot.wooden.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

/**
 * 사용자 리포지토리
 * - 이메일(loginId) 중복 체크/단건 조회
 * - 운영 리스트 검색(이메일/이름 LIKE, 대소문자 무시)
 */
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByLoginId(String loginId);

    Optional<User> findByLoginId(String loginId);
}
