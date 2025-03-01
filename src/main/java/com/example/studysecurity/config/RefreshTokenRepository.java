package com.example.studysecurity.config; // 패키지 선언: 해당 인터페이스가 속한 패키지를 지정

import org.springframework.data.jpa.repository.JpaRepository; // Spring Data JPA의 JpaRepository를 사용하기 위해 import
import java.util.Optional; // Optional을 사용하여 null 처리를 안전하게 하기 위해 import

// RefreshToken 엔티티의 데이터베이스 접근을 담당하는 JPA 리포지토리 인터페이스
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // userId를 기준으로 RefreshToken을 조회하는 메서드
    // Optional로 감싸서 반환하여, 토큰이 존재하지 않을 경우 null 대신 빈 Optional을 반환하도록 함
    Optional<RefreshToken> findByUserId(Long userId);
}
