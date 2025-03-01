package com.example.studysecurity; // 패키지 선언: 해당 클래스가 속한 패키지를 지정

import lombok.Getter; // Lombok의 @Getter를 사용하여 모든 필드의 Getter 메서드를 자동 생성

@Getter // 모든 필드에 대한 Getter 메서드를 Lombok을 사용하여 자동 생성
public class GoogleAccessTokenRequest { // Google OAuth2 액세스 토큰 요청 정보를 담는 클래스

    private final String code; // Google에서 발급한 인증 코드
    private final String client_id; // OAuth2 클라이언트 ID
    private final String client_secret; // OAuth2 클라이언트 비밀키
    private final String redirect_uri; // OAuth2 인증 후 리디렉션될 URI
    private final String grant_type; // 인증 유형 (예: "authorization_code")

    // 생성자: Google OAuth2 토큰 요청에 필요한 정보를 매개변수로 받아 초기화
    public GoogleAccessTokenRequest(String code, String clientId, String clientSecret, String redirectUri, String authorizationCode) {
        this.code = code; // Google에서 발급한 인증 코드 설정
        this.client_id = clientId; // 클라이언트 ID 설정
        this.client_secret = clientSecret; // 클라이언트 비밀키 설정
        this.redirect_uri = redirectUri; // 리디렉션 URI 설정
        this.grant_type = authorizationCode; // "authorization_code"를 grant_type으로 설정
    }
}
