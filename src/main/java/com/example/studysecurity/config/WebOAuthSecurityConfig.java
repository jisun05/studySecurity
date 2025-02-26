package com.example.studysecurity.config;

import com.example.studysecurity.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.example.studysecurity.config.oauth.OAuth2SuccessHandler;
import com.example.studysecurity.config.oauth.OAuth2UserCustomService;
import com.example.studysecurity.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfig {

    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final MemberService memberService;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers("/img/**", "/css/**", "/js/**");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 비활성화 및 세션 정책 Stateless 설정
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // /oauthLogin, /login/google, /login/success는 인증 없이 접근 가능하도록 허용
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/oauthLogin", "/login/google", "/login/success").permitAll()
                .anyRequest().authenticated()
        );

        // OAuth2 로그인 설정 (로그인 페이지를 /oauthLogin으로 지정)
        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/oauthLogin")
                .authorizationEndpoint(authorization -> authorization
                        .authorizationRequestRepository(authorizationRequestRepository)
                )
                .successHandler(oAuth2SuccessHandler)
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(oAuth2UserCustomService)
                )
        );

        // 로그아웃 설정 (로그아웃 성공 후 /oauthLogin으로 리다이렉트)
        http.logout(logout -> logout.logoutSuccessUrl("/oauthLogin"));

        // API 접근 시 인증 실패에 대해 401(UNAUTHORIZED) 상태 코드 반환
        http.exceptionHandling(exception -> exception
                .defaultAuthenticationEntryPointFor(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        new AntPathRequestMatcher("/api/**")
                )
        );

        return http.build();
    }
}
