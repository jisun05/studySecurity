// com.example.studysecurity.config.oauth 패키지에 속함을 선언합니다.
package com.example.studysecurity.config.oauth;

// CookieUtil: 쿠키 관련 유틸리티 기능 제공 클래스 import
import com.example.studysecurity.config.CookieUtil;
// RefreshToken: 리프레시 토큰 정보를 담은 도메인 클래스 import
import com.example.studysecurity.config.RefreshToken;
// RefreshTokenRepository: 리프레시 토큰 저장소 인터페이스 import
import com.example.studysecurity.config.RefreshTokenRepository;
// TokenProvider: JWT 토큰 생성 및 검증을 담당하는 클래스 import
import com.example.studysecurity.config.jwt.TokenProvider;
// Member: 회원 정보를 담은 도메인 클래스 import
import com.example.studysecurity.domain.Member;
// MemberService: 회원 관련 비즈니스 로직을 처리하는 서비스 클래스 import
import com.example.studysecurity.service.MemberService;
// Lombok의 @RequiredArgsConstructor 어노테이션 import (final 필드에 대한 생성자 자동 생성)
import lombok.RequiredArgsConstructor;
// Spring Security의 Authentication 관련 클래스 import
import org.springframework.security.core.Authentication;
// OAuth2User: OAuth2 인증 사용자 정보를 담는 인터페이스 import
import org.springframework.security.oauth2.core.user.OAuth2User;
// OAuth2 인증 성공 후 처리 핸들러로 사용되는 SimpleUrlAuthenticationSuccessHandler import
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
// 스프링 빈으로 등록하기 위한 어노테이션 import
import org.springframework.stereotype.Component;
// URI 빌더를 제공하는 클래스 import
import org.springframework.web.util.UriComponentsBuilder;
// HTTP 요청 객체 import
import jakarta.servlet.http.HttpServletRequest;
// HTTP 응답 객체 import
import jakarta.servlet.http.HttpServletResponse;
// 입출력 관련 예외 처리를 위한 IOException import
import java.io.IOException;
// 토큰 만료 기간 등을 정의하기 위한 Duration import
import java.time.Duration;

@RequiredArgsConstructor // final 필드에 대한 생성자를 Lombok이 자동으로 생성합니다.
@Component // 이 클래스를 스프링 빈으로 등록하여 관리합니다.
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    // 리프레시 토큰 쿠키의 이름을 정의합니다.
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    // 리프레시 토큰의 유효 기간을 14일로 설정합니다.
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    // 액세스 토큰의 유효 기간을 1일로 설정합니다.
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);
    // 인증 성공 후 리다이렉트할 URL 경로를 지정합니다.
    public static final String REDIRECT_PATH = "/login/success";

    // JWT 토큰 생성을 위한 토큰 프로바이더 주입
    private final TokenProvider tokenProvider;
    // 리프레시 토큰 저장/관리를 위한 저장소 주입
    private final RefreshTokenRepository refreshTokenRepository;
    // 회원 관련 로직을 처리하는 서비스 주입
    private final MemberService memberService;

    // 인증 성공 시 호출되는 메서드로, OAuth2 로그인 성공 후 처리 로직을 정의합니다.
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // OAuth2 인증 정보를 담고 있는 객체에서 OAuth2User 정보를 가져옵니다.
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        // OAuth2User의 속성을 활용하여 Google OAuth2 사용자 정보를 생성합니다.
        OAuth2UserInfo oAuth2UserInfo = new GoogleOAuth2UserInfo(oAuth2User.getAttributes());

        // 회원 정보를 DB에서 조회하거나, 존재하지 않으면 신규 등록합니다.
        Member member = memberService.saveOrUpdate(oAuth2UserInfo);

        // 회원의 ID와 정해진 유효기간(14일)을 사용하여 리프레시 토큰을 생성합니다.
        String refreshToken = tokenProvider.generateToken(member.getId().toString(), REFRESH_TOKEN_DURATION);
        // 리프레시 토큰을 저장소에 저장하거나 업데이트합니다.
        saveRefreshToken(member.getId(), refreshToken);
        // 생성된 리프레시 토큰을 쿠키에 추가합니다.
        addRefreshTokenToCookie(request, response, refreshToken);

        // 회원의 ID와 정해진 유효기간(1일)을 사용하여 액세스 토큰을 생성합니다.
        String accessToken = tokenProvider.generateToken(member.getId().toString(), ACCESS_TOKEN_DURATION);
        // 액세스 토큰을 파라미터로 포함하는 리다이렉트 URL을 구성합니다.
        String targetUrl = getTargetUrl(accessToken);

        // 인증 관련 세션, 쿠키 등 임시 데이터를 제거합니다.
        clearAuthenticationAttributes(request);
        // 구성한 URL로 클라이언트를 리다이렉트합니다.
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    // 회원 ID와 새로운 리프레시 토큰을 저장소에 저장하거나 업데이트하는 메서드입니다.
    private void saveRefreshToken(Long memberId, String newRefreshToken) {
        // 회원 ID로 기존의 리프레시 토큰을 찾고, 존재하면 업데이트, 없으면 새로 생성합니다.
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(memberId)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(memberId, newRefreshToken));
        // 리프레시 토큰을 저장소에 저장합니다.
        refreshTokenRepository.save(refreshToken);
    }

    // 생성된 리프레시 토큰을 HTTP 쿠키에 추가하는 메서드입니다.
    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        // 리프레시 토큰 쿠키의 최대 수명을 초 단위로 계산합니다.
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();
        // 기존에 존재하는 리프레시 토큰 쿠키를 삭제합니다.
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        // 새로운 리프레시 토큰 쿠키를 생성하고 응답에 추가합니다.
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    // 액세스 토큰을 쿼리 파라미터로 포함하여 리다이렉트할 URL을 구성하는 메서드입니다.
    private String getTargetUrl(String token) {
        // UriComponentsBuilder를 사용하여 REDIRECT_PATH에 "token" 파라미터를 추가하고, 최종 URL 문자열을 반환합니다.
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
                .queryParam("token", token)
                .build()
                .toUriString();
    }
}
