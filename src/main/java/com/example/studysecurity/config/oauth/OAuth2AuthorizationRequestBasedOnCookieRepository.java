// com.example.studysecurity.config.oauth 패키지에 이 클래스가 속함을 선언합니다.
package com.example.studysecurity.config.oauth;

import jakarta.servlet.http.Cookie; // HTTP 쿠키를 다루기 위한 클래스를 import 합니다.
import jakarta.servlet.http.HttpServletRequest; // HTTP 요청 정보를 다루기 위한 클래스를 import 합니다.
import jakarta.servlet.http.HttpServletResponse; // HTTP 응답 정보를 다루기 위한 클래스를 import 합니다.
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository; // OAuth2 인증 요청을 저장, 조회, 삭제할 인터페이스입니다.
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest; // OAuth2 인증 요청 정보를 담고 있는 클래스입니다.
import org.springframework.stereotype.Component; // 스프링 빈으로 등록하기 위한 어노테이션입니다.
import org.springframework.util.SerializationUtils; // 객체의 직렬화와 역직렬화를 도와주는 유틸리티 클래스입니다.
import org.springframework.web.util.WebUtils; // 웹 관련 유틸리티 메서드를 제공하는 클래스입니다.
import java.util.Base64; // Base64 인코딩 및 디코딩을 위한 클래스를 import 합니다.

@Component // 이 클래스를 스프링의 컴포넌트로 등록하여 빈으로 관리되게 합니다.
public class OAuth2AuthorizationRequestBasedOnCookieRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    // 쿠키 이름: OAuth2 인증 요청 정보를 저장하는 쿠키의 이름을 정의합니다.
    public static final String OAUTH2_AUTH_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    // 쿠키 이름: 인증 후 리다이렉트 URI를 저장하는 쿠키의 이름을 정의합니다.
    public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
    // 쿠키 만료 시간 (초): 쿠키의 유효 기간을 180초(3분)로 설정합니다.
    public static final int COOKIE_EXPIRE_SECONDS = 180;

    // HTTP 요청에서 저장된 OAuth2AuthorizationRequest를 쿠키에서 읽어오는 메서드입니다.
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        // 요청에서 지정된 이름의 쿠키를 가져옵니다.
        Cookie cookie = WebUtils.getCookie(request, OAUTH2_AUTH_REQUEST_COOKIE_NAME);
        // 쿠키가 존재한다면
        if (cookie != null) {
            // 쿠키 값(Base64로 인코딩된 문자열)을 디코딩하여 byte 배열로 변환합니다.
            byte[] decodedBytes = Base64.getDecoder().decode(cookie.getValue());
            // byte 배열을 역직렬화하여 OAuth2AuthorizationRequest 객체로 변환 후 반환합니다.
            return (OAuth2AuthorizationRequest) SerializationUtils.deserialize(decodedBytes);
        }
        // 쿠키가 없으면 null을 반환합니다.
        return null;
    }

    // OAuth2AuthorizationRequest를 쿠키에 저장하는 메서드입니다.
    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        // 전달받은 authorizationRequest가 null이면, 기존 쿠키들을 제거하고 종료합니다.
        if (authorizationRequest == null) {
            removeAuthorizationRequestCookies(request, response);
            return;
        }
        // authorizationRequest 객체를 직렬화하여 byte 배열로 변환합니다.
        byte[] serializedAuthRequest = SerializationUtils.serialize(authorizationRequest);
        // 직렬화된 byte 배열을 Base64 인코딩하여 문자열로 변환합니다.
        String encodedAuthRequest = Base64.getEncoder().encodeToString(serializedAuthRequest);
        // 인코딩된 인증 요청 정보를 저장할 쿠키 객체를 생성합니다.
        Cookie authCookie = new Cookie(OAUTH2_AUTH_REQUEST_COOKIE_NAME, encodedAuthRequest);
        authCookie.setPath("/"); // 쿠키가 모든 경로에서 사용 가능하도록 경로를 설정합니다.
        authCookie.setHttpOnly(true); // 클라이언트 측 스크립트에서 쿠키에 접근하지 못하도록 설정합니다.
        authCookie.setMaxAge(COOKIE_EXPIRE_SECONDS); // 쿠키 만료 시간을 설정합니다.
        // 응답에 인증 쿠키를 추가합니다.
        response.addCookie(authCookie);

        // 요청 파라미터에서 "redirect_uri" 값을 가져옵니다.
        String redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME);
        // 리다이렉트 URI가 존재하고 값이 비어있지 않으면
        if (redirectUriAfterLogin != null && !redirectUriAfterLogin.isEmpty()) {
            // 해당 리다이렉트 URI를 저장할 쿠키 객체를 생성합니다.
            Cookie redirectCookie = new Cookie(REDIRECT_URI_PARAM_COOKIE_NAME, redirectUriAfterLogin);
            redirectCookie.setPath("/"); // 모든 경로에서 접근 가능하도록 설정합니다.
            redirectCookie.setMaxAge(COOKIE_EXPIRE_SECONDS); // 쿠키 만료 시간을 설정합니다.
            // 응답에 리다이렉트 쿠키를 추가합니다.
            response.addCookie(redirectCookie);
        }
    }

    // 쿠키에서 OAuth2AuthorizationRequest를 제거하는 메서드입니다.
    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        // 현재 쿠키에 저장된 인증 요청 정보를 로드하여 반환합니다.
        // (실제 쿠키 삭제는 removeAuthorizationRequestCookies() 메서드에서 수행)
        return loadAuthorizationRequest(request);
    }

    // 인증 요청과 리다이렉트 URI 정보를 저장한 쿠키를 삭제하는 메서드입니다.
    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        // 요청에서 인증 요청 정보를 저장한 쿠키를 가져옵니다.
        Cookie authCookie = WebUtils.getCookie(request, OAUTH2_AUTH_REQUEST_COOKIE_NAME);
        if (authCookie != null) {
            // 쿠키 값을 빈 문자열로 설정합니다.
            authCookie.setValue("");
            authCookie.setPath("/"); // 경로를 "/"로 설정합니다.
            authCookie.setMaxAge(0); // 만료 시간을 0으로 설정하여 즉시 삭제되도록 합니다.
            // 응답에 수정된 쿠키를 추가합니다.
            response.addCookie(authCookie);
        }
        // 요청에서 리다이렉트 URI 정보를 저장한 쿠키를 가져옵니다.
        Cookie redirectCookie = WebUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME);
        if (redirectCookie != null) {
            // 쿠키 값을 빈 문자열로 설정합니다.
            redirectCookie.setValue("");
            redirectCookie.setPath("/"); // 경로를 "/"로 설정합니다.
            redirectCookie.setMaxAge(0); // 쿠키 만료 시간을 0으로 설정하여 즉시 삭제되도록 합니다.
            // 응답에 수정된 쿠키를 추가합니다.
            response.addCookie(redirectCookie);
        }
    }
}
