package com.example.studysecurity.domain; // 패키지 선언: 해당 클래스가 속한 패키지를 지정

import jakarta.persistence.*; // JPA 관련 어노테이션을 사용하기 위해 import
import lombok.*; // Lombok을 사용하여 반복적인 코드 생성을 줄이기 위해 import
import java.time.LocalDateTime; // 날짜 및 시간을 다루기 위해 import (현재 사용되지 않음)

@Entity // JPA 엔티티(Entity) 클래스로 지정하여 데이터베이스 테이블과 매핑
@Table(name = "member") // 해당 엔티티가 매핑될 테이블 이름을 "member"로 지정
@Getter // Lombok의 @Getter를 사용하여 모든 필드의 Getter 메서드를 자동 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자를 보호 수준(PROTECTED)으로 생성하여 직접 호출 방지
public class Member { // 회원 정보를 저장하는 엔티티 클래스

    @Id // 해당 필드를 테이블의 기본 키(Primary Key)로 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키를 자동 증가(AUTO_INCREMENT) 방식으로 설정
    private Long id; // 데이터베이스에서 자동 생성되는 기본 키 필드

    @Column(nullable = false, unique = true) // NULL 허용 안 함, 중복 허용 안 함 (이메일은 유일해야 함)
    private String email; // 회원의 이메일 (로그인 식별자로 사용 가능)

    @Column(nullable = false) // NULL 허용 안 함
    private String name; // 회원의 이름

    // 생성자: 회원 객체를 생성할 때 email과 name을 설정
    public Member(String email, String name) {
        this.email = email; // email 필드 초기화
        this.name = name; // name 필드 초기화
    }

    // 회원의 이름을 변경하는 메서드
    public void updateName(String name) {
        this.name = name; // name 값을 새로운 값으로 변경
    }
}
