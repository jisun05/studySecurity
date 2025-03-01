package com.example.studysecurity; // 패키지 선언: 해당 클래스가 속한 패키지를 지정

import com.fasterxml.jackson.annotation.JsonProperty; // JSON 데이터를 필드에 매핑하기 위해 사용되는 Jackson 어노테이션 import
import lombok.Getter; // Lombok의 @Getter를 사용하여 모든 필드의 Getter 메서드를 자동 생성
import lombok.NoArgsConstructor; // Lombok의 @NoArgsConstructor를 사용하여 기본 생성자를 자동 생성

@Getter // 모든 필드에 대한 Getter 메서드를 Lombok을 사용하여 자동 생성
@NoArgsConstructor // 기본 생성자를 자동으로 생성 (JSON 파싱 시 필요)
public class GoogleAccessTokenResponse { // Google OAuth2 액세스 토큰 응답 데이터를 담는 클래스

    @JsonProperty("access_token") // JSON 응답에서 "access_token" 필드를 Java의 accessToken 필드에 매핑
    private String accessToken; // OAuth2 액세스 토큰

    @JsonProperty("expires_in") // JSON 응답에서 "expires_in" 필드를 Java의 expiresIn 필드에 매핑
    private Long expiresIn; // 액세스 토큰의 만료 시간 (초 단위)

    @JsonProperty("refresh_token") // JSON 응답에서 "refresh_token" 필드를 Java의 refreshToken 필드에 매핑
    private String refreshToken; // 새 액세스 토큰을 발급받기 위한 리프레시 토큰 (선택적 제공)

    @JsonProperty("scope") // JSON 응답에서 "scope" 필드를 Java의 scope 필드에 매핑
    private String scope; // 액세스 토큰의 권한 범위 (예: "email profile")

    @JsonProperty("token_type") // JSON 응답에서 "token_type" 필드를 Java의 tokenType 필드에 매핑
    private String tokenType; // 토큰 유형 (보통 "Bearer" 값이 반환됨)
}
