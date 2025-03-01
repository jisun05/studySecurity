package com.example.studysecurity.config; // 패키지 선언: 해당 클래스가 속한 패키지를 지정

import jakarta.persistence.*; // JPA 관련 어노테이션을 사용하기 위해 import
import lombok.*; // Lombok을 사용하여 반복적인 코드 생성을 줄이기 위해 import

@Entity // JPA 엔티티(Entity) 클래스로 지정하여 데이터베이스 테이블과 매핑
@Table(name = "refresh_token") // 해당 엔티티가 매핑될 테이블 이름을 "refresh_token"으로 지정
@Getter // Lombok의 @Getter를 사용하여 모든 필드의 Getter 메서드를 자동 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자를 보호 수준(PROTECTED)으로 생성하여 직접 호출 방지
public class RefreshToken { // 리프레시 토큰을 저장하는 엔티티 클래스

    @Id // 해당 필드를 테이블의 기본 키(Primary Key)로 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키를 자동 증가(AUTO_INCREMENT) 방식으로 설정
    private Long id; // 데이터베이스에서 자동 생성되는 기본 키 필드

    @Column(name = "user_id", nullable = false, unique = true) // "user_id"라는 컬럼과 매핑, NULL 허용 안 함, 중복 허용 안 함(UNIQUE)
    private Long userId; // 해당 리프레시 토큰을 소유한 사용자의 ID

    @Column(name = "refresh_token", nullable = false) // "refresh_token"이라는 컬럼과 매핑, NULL 허용 안 함
    private String token; // 리프레시 토큰 값을 저장하는 필드

    // 특정 userId와 토큰 값을 받아서 객체를 생성하는 생성자
    public RefreshToken(Long userId, String token) {
        this.userId = userId; // userId 필드 초기화
        this.token = token; // token 필드 초기화
    }

    // 기존 리프레시 토큰을 새 토큰으로 업데이트하는 메서드
    public RefreshToken update(String newToken) {
        this.token = newToken; // 토큰 값을 새로운 값으로 변경
        return this; // 변경된 객체를 반환 (메서드 체이닝 가능)
    }
}
