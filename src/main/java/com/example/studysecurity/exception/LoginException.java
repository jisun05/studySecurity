package com.example.studysecurity.exception; // 패키지 선언: 해당 클래스가 속한 패키지를 지정

// 사용자 정의 예외 클래스 LoginException (로그인 관련 예외 처리)
public class LoginException extends RuntimeException { // RuntimeException을 상속하여 런타임 예외로 정의

    private final LoginExceptionType type; // 예외의 유형을 저장하는 필드

    // 생성자: 예외 유형을 매개변수로 받아 예외 메시지를 설정하고 예외 유형을 저장
    public LoginException(LoginExceptionType type) {
        super(type.getMessage()); // 부모 클래스(RuntimeException)의 생성자에 예외 메시지를 전달
        this.type = type; // 예외 유형 저장
    }

    // 예외 유형을 반환하는 Getter 메서드
    public LoginExceptionType getType() {
        return type;
    }
}
