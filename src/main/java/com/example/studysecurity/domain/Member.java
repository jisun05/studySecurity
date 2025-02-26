package com.example.studysecurity.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    // 추가 필드 및 생성자 등

    public Member(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public void updateName(String name) {
        this.name = name;
    }
}
