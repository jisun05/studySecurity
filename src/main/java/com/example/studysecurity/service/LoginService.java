package com.example.studysecurity.service; // 패키지 선언: 해당 클래스가 속한 패키지를 지정

import com.example.studysecurity.GoogleAccountProfileResponse; // Google 계정 프로필 응답 데이터를 담는 클래스 import
import com.example.studysecurity.GoogleClient; // Google API 클라이언트를 다루는 클래스 import
import com.example.studysecurity.JwtTokenGenerator; // JWT 토큰을 생성하는 클래스 import
import com.example.studysecurity.domain.Member; // 회원(Member) 엔티티 import
import lombok.RequiredArgsConstructor; // Lombok의 @RequiredArgsConstructor를 사용하여 final 필드의 생성자를 자동 생성
import org.springframework.stereotype.Service; // 스프링의 서비스(Service) 계층을 나타내는 어노테이션 import

@Service // 해당 클래스가 서비스(Service) 컴포넌트임을 나타내며, 스프링 빈(Bean)으로 등록됨
@RequiredArgsConstructor // Lombok을 사용하여 final 필드에 대한 생성자를 자동으로 생성
public class LoginService { // 로그인 관련 로직을 처리하는 서비스 클래스

    private final GoogleClient googleClient; // Google API와 통신하여 사용자 정보를 가져오는 클라이언트 객체
    private final MemberService memberService; // 회원 정보를 저장 및 조회하는 서비스 객체
    private final JwtTokenGenerator jwtTokenGenerator; // JWT 토큰을 생성하는 객체

    // Google OAuth2를 이용한 로그인 처리 메서드
    public String loginWithGoogle(String code) {
        // Google API를 호출하여 인증 코드(code)를 이용해 사용자 프로필 정보를 가져옴
        GoogleAccountProfileResponse profile = googleClient.getGoogleAccountProfile(code);

        // 가져온 사용자 정보를 기반으로 회원을 저장하거나 업데이트
        Member member = memberService.saveOrUpdate(
                new com.example.studysecurity.config.oauth.GoogleOAuth2UserInfo(
                        java.util.Map.of("email", profile.getEmail(), "name", profile.getName()) // Google 사용자 정보를 Map 형태로 전달
                )
        );

        // 회원의 ID를 기반으로 JWT 토큰을 생성하여 반환 (예: 1시간 유효한 토큰)
        return jwtTokenGenerator.generateToken(member.getId().toString());
    }
}
