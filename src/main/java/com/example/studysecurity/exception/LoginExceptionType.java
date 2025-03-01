package com.example.studysecurity.exception; // 패키지 선언: 해당 클래스가 속한 패키지를 지정

// 로그인 관련 예외 유형을 정의하는 열거형(enum)
public enum LoginExceptionType {

    // 정의된 예외 유형 및 메시지
    NOT_FOUND_GOOGLE_ACCESS_TOKEN_RESPONSE("Could not find Google access token response"), // Google 액세스 토큰 응답을 찾을 수 없음
    INVALID_ACCESS_TOKEN("Invalid access token"), // 유효하지 않은 액세스 토큰
    INVALID_GOOGLE_PROFILE("Invalid Google profile"); // 유효하지 않은 Google 프로필

    private final String message; // 예외 메시지를 저장하는 필드

    // 생성자: 예외 메시지를 매개변수로 받아 필드에 저장
    LoginExceptionType(String message) {
        this.message = message;
    }

    // 예외 메시지를 반환하는 Getter 메서드
    public String getMessage() {
        return message;
    }
}
