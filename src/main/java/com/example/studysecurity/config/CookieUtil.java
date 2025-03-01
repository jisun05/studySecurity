package com.example.studysecurity.config; // 패키지 선언: 해당 클래스가 포함된 패키지를 지정

import jakarta.servlet.http.Cookie; // HTTP 쿠키 관련 클래스를 사용하기 위해 import
import jakarta.servlet.http.HttpServletRequest; // HTTP 요청 정보를 다루는 클래스 import
import jakarta.servlet.http.HttpServletResponse; // HTTP 응답을 다루는 클래스 import

public class CookieUtil { // 쿠키 관련 유틸리티 메서드를 제공하는 클래스 선언

    // 새로운 쿠키를 생성하고 응답(Response)에 추가하는 메서드
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value); // 지정된 이름(name)과 값(value)을 가진 새로운 쿠키 객체 생성
        cookie.setPath("/"); // 쿠키의 적용 경로를 루트("/")로 설정하여 모든 경로에서 사용 가능하도록 설정
        cookie.setMaxAge(maxAge); // 쿠키의 유효 시간을 설정 (초 단위)
        response.addCookie(cookie); // 응답에 쿠키를 추가하여 클라이언트에게 전송
    }

    // 특정 이름(name)의 쿠키를 삭제하는 메서드
    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies(); // 요청(Request)에서 쿠키 목록을 가져옴
        if (cookies != null) { // 쿠키가 존재하는 경우에만 실행
            for (Cookie cookie : cookies) { // 모든 쿠키를 순회하며 특정 쿠키를 찾음
                if (cookie.getName().equals(name)) { // 현재 쿠키의 이름이 삭제할 쿠키의 이름과 일치하는지 확인
                    cookie.setValue(""); // 쿠키의 값을 빈 문자열("")로 변경
                    cookie.setPath("/"); // 쿠키의 적용 경로를 루트("/")로 설정
                    cookie.setMaxAge(0); // 쿠키의 유효 시간을 0으로 설정하여 즉시 만료되도록 함
                    response.addCookie(cookie); // 변경된 쿠키를 응답에 추가하여 클라이언트에게 전송
                }
            }
        }
    }
}
