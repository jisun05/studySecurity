package com.example.studysecurity.service; // 패키지 선언: 해당 클래스가 속한 패키지를 지정

import com.example.studysecurity.config.oauth.OAuth2UserInfo; // OAuth2 로그인 시 가져오는 사용자 정보를 담는 클래스 import
import com.example.studysecurity.domain.Member; // Member 엔티티 import
import com.example.studysecurity.domain.MemberRepository; // Member 엔티티를 관리하는 JPA 리포지토리 import
import lombok.RequiredArgsConstructor; // Lombok의 @RequiredArgsConstructor를 사용하여 final 필드의 생성자를 자동 생성
import org.springframework.stereotype.Service; // 스프링의 서비스(Service) 계층을 나타내는 어노테이션 import

import java.util.Optional; // Optional을 사용하여 null 처리를 안전하게 하기 위해 import

@Service // 해당 클래스가 서비스(Service) 컴포넌트임을 나타내며, 스프링 빈(Bean)으로 등록됨
@RequiredArgsConstructor // Lombok을 사용하여 final 필드에 대한 생성자를 자동으로 생성
public class MemberService { // 회원 정보를 관리하는 서비스 클래스

    private final MemberRepository memberRepository; // Member 엔티티의 데이터베이스 접근을 담당하는 JPA 리포지토리 객체

    // OAuth2 사용자 정보를 기반으로 회원 정보를 저장 또는 업데이트하는 메서드
    public Member saveOrUpdate(OAuth2UserInfo oAuth2UserInfo) {
        String email = oAuth2UserInfo.getEmail(); // OAuth2UserInfo에서 이메일 정보를 가져옴
        Optional<Member> optionalMember = memberRepository.findByEmail(email); // 이메일을 기준으로 회원을 조회

        Member member; // Member 객체 선언

        if (optionalMember.isPresent()) { // 기존 회원이 존재하는 경우
            member = optionalMember.get(); // 기존 회원 객체 가져오기
            member.updateName(oAuth2UserInfo.getName()); // 회원의 이름을 새로운 값으로 업데이트
        } else { // 기존 회원이 존재하지 않는 경우 (신규 가입)
            member = new Member(email, oAuth2UserInfo.getName()); // 새로운 회원 객체 생성
            member = memberRepository.save(member); // 회원 정보를 데이터베이스에 저장
        }

        return member; // 업데이트 또는 저장된 회원 객체 반환
    }
}
