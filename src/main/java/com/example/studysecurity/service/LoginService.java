package com.example.studysecurity.service;

import com.example.studysecurity.GoogleAccountProfileResponse;
import com.example.studysecurity.GoogleClient;
import com.example.studysecurity.JwtTokenGenerator;
import com.example.studysecurity.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final GoogleClient googleClient;
    private final MemberService memberService;
    private final JwtTokenGenerator jwtTokenGenerator;

    public String loginWithGoogle(String code) {
        GoogleAccountProfileResponse profile = googleClient.getGoogleAccountProfile(code);
        Member member = memberService.saveOrUpdate(new com.example.studysecurity.config.oauth.GoogleOAuth2UserInfo(
                java.util.Map.of("email", profile.getEmail(), "name", profile.getName())
        ));
        return jwtTokenGenerator.generateToken(member.getId().toString()); // 예: 1시간 토큰
    }
}
