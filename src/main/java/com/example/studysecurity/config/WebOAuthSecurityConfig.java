package com.example.studysecurity.config; // 패키지 선언: 해당 클래스가 속한 패키지를 지정

import com.example.studysecurity.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository; // OAuth2 인증 요청을 쿠키 기반으로 처리하는 클래스 import
import com.example.studysecurity.config.oauth.OAuth2SuccessHandler; // OAuth2 로그인 성공 시 실행될 핸들러 클래스 import
import com.example.studysecurity.config.oauth.OAuth2UserCustomService; // OAuth2 사용자 정보를 가져오는 서비스 import
import com.example.studysecurity.service.MemberService; // 회원 관련 비즈니스 로직을 처리하는 서비스 import
import lombok.RequiredArgsConstructor; // 생성자를 통한 의존성 주입을 자동으로 생성하는 Lombok 어노테이션 import
import org.springframework.context.annotation.Bean; // @Bean 어노테이션을 사용하기 위해 import
import org.springframework.context.annotation.Configuration; // @Configuration 어노테이션을 사용하기 위해 import
import org.springframework.http.HttpStatus; // HTTP 상태 코드를 사용하기 위해 import
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // Spring Security의 HTTP 보안 설정을 다루는 클래스 import
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer; // 특정 요청을 보안 필터에서 제외하기 위해 import
import org.springframework.security.config.http.SessionCreationPolicy; // 세션 정책을 설정하기 위해 import
import org.springframework.security.web.SecurityFilterChain; // 보안 필터 체인을 구성하기 위해 import
import org.springframework.security.web.authentication.HttpStatusEntryPoint; // 인증 실패 시 HTTP 상태 코드를 반환하는 클래스 import
import org.springframework.security.web.util.matcher.AntPathRequestMatcher; // 특정 URL 패턴과 요청을 매칭하는 클래스 import

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console; // H2 콘솔 요청을 허용하기 위해 import

@RequiredArgsConstructor // Lombok을 사용하여 final 필드에 대한 생성자를 자동으로 생성
@Configuration // 해당 클래스가 스프링의 설정 클래스임을 나타냄
public class WebOAuthSecurityConfig { // OAuth2 보안을 설정하는 클래스

    private final OAuth2UserCustomService oAuth2UserCustomService; // OAuth2 사용자 정보를 관리하는 서비스
    private final OAuth2SuccessHandler oAuth2SuccessHandler; // OAuth2 로그인 성공 후 처리할 핸들러
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository; // OAuth2 인증 요청을 쿠키 기반으로 처리하는 저장소
    private final MemberService memberService; // 사용자 관련 비즈니스 로직을 처리하는 서비스

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() { // 특정 요청을 보안 필터에서 제외하는 설정을 위한 메서드
        return web -> web.ignoring()
                .requestMatchers(toH2Console()) // H2 데이터베이스 콘솔 요청은 보안 필터에서 제외
                .requestMatchers("/img/**", "/css/**", "/js/**"); // 정적 리소스(img, css, js) 요청을 보안 필터에서 제외
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception { // 보안 필터 체인 설정을 위한 메서드
        // CSRF 보호 기능 비활성화 및 세션을 사용하지 않는 Stateless 설정
        http.csrf(csrf -> csrf.disable()) // CSRF(Cross-Site Request Forgery) 보호 비활성화
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // 세션을 생성하지 않는 Stateless 정책 적용

        // 특정 엔드포인트(oauthLogin, login/google, login/success)는 인증 없이 접근 허용
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/oauthLogin", "/login/google", "/login/success").permitAll() // 로그인 관련 페이지는 인증 없이 접근 가능
                .anyRequest().authenticated() // 위에서 지정한 엔드포인트를 제외한 모든 요청은 인증 필요
        );

        // OAuth2 로그인 설정 (로그인 페이지를 "/oauthLogin"으로 지정)
        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/oauthLogin") // OAuth2 로그인 페이지 설정
                .authorizationEndpoint(authorization -> authorization
                        .authorizationRequestRepository(authorizationRequestRepository) // OAuth2 인증 요청을 쿠키 기반으로 저장
                )
                .successHandler(oAuth2SuccessHandler) // 로그인 성공 시 실행할 핸들러 지정
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(oAuth2UserCustomService) // OAuth2 로그인 후 사용자 정보를 가져오는 서비스 지정
                )
        );

        // 로그아웃 설정 (로그아웃 성공 후 "/oauthLogin"으로 리다이렉트)
        http.logout(logout -> logout.logoutSuccessUrl("/oauthLogin"));

        // API 접근 시 인증 실패에 대해 401(UNAUTHORIZED) 상태 코드 반환
        http.exceptionHandling(exception -> exception
                .defaultAuthenticationEntryPointFor(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), // 인증되지 않은 요청에 대해 401(Unauthorized) 응답 반환
                        new AntPathRequestMatcher("/api/**") // "/api/**" 경로에 대한 요청에 적용
                )
        );

        return http.build(); // 설정된 SecurityFilterChain 반환
    }
}
