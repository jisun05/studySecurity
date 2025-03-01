package com.example.studysecurity; // 패키지 선언: 해당 클래스가 속한 패키지를 지정

import lombok.Getter; // Lombok의 @Getter를 사용하여 모든 필드의 Getter 메서드를 자동 생성
import lombok.NoArgsConstructor; // Lombok의 @NoArgsConstructor를 사용하여 기본 생성자를 자동 생성

@Getter // 모든 필드에 대한 Getter 메서드를 Lombok을 사용하여 자동 생성
@NoArgsConstructor // 기본 생성자를 자동으로 생성 (JSON 파싱 시 필요)
public class GoogleAccountProfileResponse { // Google OAuth2 사용자 프로필 응답 데이터를 담는 클래스

    private String email; // 사용자의 이메일 주소
    private String name; // 사용자의 이름

    // 필요 시 추가 필드 (예: 프로필 이미지, 언어 설정 등)
    // private String profileImage; // 사용자의 프로필 이미지 URL
    // private String locale; // 사용자의 언어 및 지역 설정
}
