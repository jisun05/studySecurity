package com.example.studysecurity; // 패키지 선언: 해당 클래스가 속한 패키지를 지정

import com.example.studysecurity.exception.LoginException; // 로그인 예외 처리를 위한 사용자 정의 예외 클래스 import
import lombok.RequiredArgsConstructor; // Lombok의 @RequiredArgsConstructor를 사용하여 final 필드의 생성자를 자동 생성
import org.springframework.beans.factory.annotation.Value; // application.properties 또는 application.yml에서 값을 주입받기 위해 import
import org.springframework.http.HttpEntity; // HTTP 요청 본문을 나타내는 클래스 import
import org.springframework.http.HttpHeaders; // HTTP 요청 헤더를 설정하기 위한 클래스 import
import org.springframework.http.HttpMethod; // HTTP 요청 메서드를 나타내는 Enum import (GET, POST 등)
import org.springframework.http.MediaType; // HTTP 요청 및 응답의 미디어 타입을 정의하기 위한 클래스 import
import org.springframework.stereotype.Component; // 스프링의 @Component 어노테이션을 사용하여 빈(Bean)으로 등록
import org.springframework.web.client.RestTemplate; // REST API 호출을 수행하기 위한 Spring 제공 클래스 import

import java.net.URLDecoder; // URL 디코딩을 위한 클래스 import
import java.nio.charset.StandardCharsets; // 문자 인코딩을 UTF-8로 설정하기 위해 import
import java.util.Optional; // null 처리를 안전하게 하기 위해 Optional 클래스 import

import static com.example.studysecurity.exception.LoginExceptionType.NOT_FOUND_GOOGLE_ACCESS_TOKEN_RESPONSE; // 로그인 예외 유형 import

@Component // 해당 클래스를 스프링 빈으로 등록
@RequiredArgsConstructor // Lombok을 사용하여 final 필드의 생성자를 자동으로 생성
public class GoogleClient { // Google API와 통신을 담당하는 클래스

    // application.yml 또는 application.properties에서 Google OAuth2 관련 설정 값을 주입받음
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId; // Google OAuth2 클라이언트 ID

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret; // Google OAuth2 클라이언트 비밀키

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri; // Google OAuth2 인증 후 리디렉션될 URI

    @Value("${spring.security.oauth2.client.registration.google.authorization-grant-type}")
    private String authorizationCode; // Google OAuth2 인증 유형 (보통 "authorization_code")

    @Value("${url.access-token}")
    private String accessTokenUrl; // Google 액세스 토큰을 요청하는 URL

    @Value("${url.profile}")
    private String profileUrl; // Google 사용자 프로필 정보를 요청하는 URL

    private final RestTemplate restTemplate; // HTTP 요청을 수행하는 RestTemplate 객체

    // Google OAuth2 인증 코드로 사용자 프로필 정보를 가져오는 메서드
    public GoogleAccountProfileResponse getGoogleAccountProfile(final String code) {
        final String accessToken = requestGoogleAccessToken(code); // 액세스 토큰 요청
        return requestGoogleAccountProfile(accessToken); // 액세스 토큰을 사용하여 사용자 프로필 요청
    }

    // Google에 액세스 토큰을 요청하는 메서드
    private String requestGoogleAccessToken(final String code) {
        final String decodedCode = URLDecoder.decode(code, StandardCharsets.UTF_8); // URL 인코딩된 인증 코드를 디코딩
        final HttpHeaders headers = new HttpHeaders(); // HTTP 요청 헤더 객체 생성
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE); // JSON 형식의 응답을 받기 위해 Accept 헤더 추가

        // Google 액세스 토큰 요청 객체를 생성하여 HTTP 요청 본문을 구성
        final HttpEntity<GoogleAccessTokenRequest> httpEntity = new HttpEntity<>(
                new GoogleAccessTokenRequest(decodedCode, clientId, clientSecret, redirectUri, authorizationCode),
                headers
        );

        // Google API에 POST 요청을 보내서 액세스 토큰을 요청
        final GoogleAccessTokenResponse response = restTemplate.exchange(
                accessTokenUrl, HttpMethod.POST, httpEntity, GoogleAccessTokenResponse.class
        ).getBody();

        // 응답이 null이면 예외 발생, 아니면 액세스 토큰 반환
        return Optional.ofNullable(response)
                .orElseThrow(() -> new LoginException(NOT_FOUND_GOOGLE_ACCESS_TOKEN_RESPONSE))
                .getAccessToken();
    }

    // Google 액세스 토큰을 사용하여 사용자 프로필 정보를 요청하는 메서드
    private GoogleAccountProfileResponse requestGoogleAccountProfile(final String accessToken) {
        final HttpHeaders headers = new HttpHeaders(); // HTTP 요청 헤더 객체 생성
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken); // 액세스 토큰을 Authorization 헤더에 추가

        // 헤더만 포함된 HTTP 요청 객체 생성
        final HttpEntity<GoogleAccessTokenRequest> httpEntity = new HttpEntity<>(headers);

        // Google API에 GET 요청을 보내서 사용자 프로필 정보를 요청
        return restTemplate.exchange(profileUrl, HttpMethod.GET, httpEntity, GoogleAccountProfileResponse.class)
                .getBody();
    }
}
