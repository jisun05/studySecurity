// com.example.studysecurity.config.oauth 패키지에 이 클래스가 속함을 선언합니다.
package com.example.studysecurity.config.oauth;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService; // 기본 OAuth2 사용자 정보 서비스를 제공하는 클래스를 import 합니다.
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest; // OAuth2 사용자 요청 정보를 담은 객체를 import 합니다.
import org.springframework.security.oauth2.core.user.OAuth2User; // OAuth2 인증 사용자 정보를 나타내는 인터페이스를 import 합니다.
import org.springframework.stereotype.Service; // 스프링 빈으로 등록하기 위한 어노테이션을 import 합니다.

@Service // 이 클래스를 스프링의 서비스 빈으로 등록하여 관리합니다.
public class OAuth2UserCustomService extends DefaultOAuth2UserService {
    // OAuth2 사용자 정보를 로드하는 메서드를 오버라이드합니다.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
        // 부모 클래스의 loadUser() 메서드를 호출하여 OAuth2 사용자 정보를 로드합니다.
        OAuth2User oAuth2User = super.loadUser(userRequest);
        // 추가 처리를 할 수 있는 위치 (예: 사용자 정보 변환, 추가 속성 설정 등)
        // 예를 들어, 로드한 사용자 정보를 기반으로 추가 정보를 설정할 수 있습니다.
        return oAuth2User; // 최종적으로 처리된 OAuth2User 객체를 반환합니다.
    }
}
