// com.example.studysecurity.config.oauth 패키지에 이 인터페이스가 속함을 선언합니다.
package com.example.studysecurity.config.oauth;

// OAuth2 인증 사용자 정보 처리를 위한 인터페이스를 선언합니다.
public interface OAuth2UserInfo {
    // OAuth2 인증 사용자로부터 이메일 정보를 가져오기 위한 메서드 선언
    String getEmail();
    // OAuth2 인증 사용자로부터 이름 정보를 가져오기 위한 메서드 선언
    String getName();
}
