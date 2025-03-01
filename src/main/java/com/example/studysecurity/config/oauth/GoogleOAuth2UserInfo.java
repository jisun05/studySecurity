// com.example.studysecurity.config.oauth 패키지에 이 클래스가 속함을 선언합니다.
package com.example.studysecurity.config.oauth;

import java.util.Map; // 다양한 타입의 데이터를 저장하는 Map 인터페이스를 import 합니다.

// OAuth2UserInfo 인터페이스를 구현하는 GoogleOAuth2UserInfo 클래스 선언
public class GoogleOAuth2UserInfo implements OAuth2UserInfo {
    // OAuth 인증 결과로부터 받은 사용자 속성을 저장할 Map 필드입니다.
    private final Map<String, Object> attributes;

    // 생성자: Google OAuth 인증 후 받은 사용자 속성(Map)을 초기화합니다.
    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    // OAuth2UserInfo 인터페이스에서 정의한 getEmail() 메서드를 구현합니다.
    @Override
    public String getEmail() {
        // attributes Map에서 "email" 키에 해당하는 값을 String 타입으로 반환합니다.
        return (String) attributes.get("email");
    }

    // OAuth2UserInfo 인터페이스에서 정의한 getName() 메서드를 구현합니다.
    @Override
    public String getName() {
        // attributes Map에서 "name" 키에 해당하는 값을 String 타입으로 반환합니다.
        return (String) attributes.get("name");
    }
}
