package com.example.studysecurity; // 패키지 선언: 해당 클래스가 속한 패키지를 지정

import io.jsonwebtoken.Claims; // JWT의 클레임(Claims)을 다루는 클래스 import
import io.jsonwebtoken.Jwts; // JWT 토큰을 생성 및 파싱하는 JJWT 라이브러리 import
import io.jsonwebtoken.SignatureAlgorithm; // JWT 서명(Signature)에 사용되는 알고리즘 import
import io.jsonwebtoken.io.Decoders; // Base64 디코딩을 위한 클래스 import
import io.jsonwebtoken.security.Keys; // JWT 서명에 사용할 키를 생성하는 클래스 import
import org.springframework.beans.factory.annotation.Value; // application.yml에서 값을 주입받기 위해 import
import org.springframework.stereotype.Component; // 해당 클래스를 스프링 빈으로 등록하기 위해 import

import java.util.Date; // JWT의 만료 시간 설정을 위해 Date 클래스 import

@Component // 해당 클래스를 스프링의 컴포넌트(Bean)로 등록
public class JwtTokenGenerator { // JWT 토큰을 생성하고 파싱하는 기능을 담당하는 클래스

    @Value("${jwt.secret_key}") // application.yml에서 JWT 서명에 사용할 비밀키(secret_key) 값을 가져옴
    private String secretKey;

    @Value("${jwt.expire-length}") // application.yml에서 JWT의 만료 시간(expire-length) 값을 가져옴
    private long expireTimeMilliSecond; // 토큰 만료 시간 (밀리초 단위)

    // JWT 토큰을 생성하는 메서드
    public String generateToken(final String id) {
        if (secretKey == null || secretKey.isBlank()) { // 비밀키가 설정되지 않았을 경우 예외 발생
            throw new IllegalStateException("jwt.secret_key가 application.yml에 설정되지 않았습니다.");
        }

        final Claims claims = Jwts.claims(); // JWT 클레임(Claims) 객체 생성
        claims.put("memberId", id); // 클레임에 회원 ID 저장

        final Date now = new Date(); // 현재 시간 저장
        final Date expiredDate = new Date(now.getTime() + expireTimeMilliSecond); // 현재 시간 + 만료 시간 = 토큰 만료 시간 설정

        return Jwts.builder() // JWT 빌더 시작
                .setClaims(claims) // 클레임 설정
                .setIssuedAt(now) // 토큰 발급 시간 설정
                .setExpiration(expiredDate) // 토큰 만료 시간 설정
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)), SignatureAlgorithm.HS256) // HMAC SHA-256 알고리즘을 사용해 서명 생성
                .compact(); // JWT 토큰을 문자열로 변환 후 반환
    }

    // JWT 토큰에서 회원 ID를 추출하는 메서드
    public String extractMemberId(final String token) {
        try {
            return Jwts.parserBuilder() // JWT 파서를 생성
                    .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))) // 서명 검증을 위한 비밀키 설정
                    .build() // JWT 파서 빌드
                    .parseClaimsJws(token) // JWT 토큰을 파싱하여 클레임 추출
                    .getBody() // 클레임(Claims) 객체 반환
                    .get("memberId", String.class); // "memberId" 필드 값을 String 타입으로 반환
        } catch (final Exception error) { // JWT 파싱 중 예외 발생 시 처리
            throw new RuntimeException("Invalid access token", error); // 예외 발생 시 "Invalid access token" 메시지와 함께 예외 던지기
        }
    }
}
