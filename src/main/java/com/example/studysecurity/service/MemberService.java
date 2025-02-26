package com.example.studysecurity.service;

import com.example.studysecurity.config.oauth.OAuth2UserInfo;
import com.example.studysecurity.domain.Member;
import com.example.studysecurity.domain.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Member saveOrUpdate(OAuth2UserInfo oAuth2UserInfo) {
        String email = oAuth2UserInfo.getEmail();
        Optional<Member> optionalMember = memberRepository.findByEmail(email);
        Member member;
        if (optionalMember.isPresent()) {
            member = optionalMember.get();
            member.updateName(oAuth2UserInfo.getName());
        } else {
            member = new Member(email, oAuth2UserInfo.getName());
            member = memberRepository.save(member);
        }
        return member;
    }
}
