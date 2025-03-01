// com.example.studysecurity.config.jwt 패키지에 이 클래스가 속함을 선언합니다.
package com.example.studysecurity.config.jwt;

// JWT 관련 Claim(토큰에 포함된 정보) 처리를 위한 클래스들을 import 합니다.
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

// 스프링에서 프로퍼티 값을 주입받기 위해 사용합니다.
import org.springframework.beans.factory.annotation.Value;
// 스프링 빈으로 등록하기 위한 어노테이션입니다.
import org.springframework.stereotype.Component;

// 문자열을 바이트 배열로 변환할 때 사용할 인코딩 방식 관련 클래스를 import 합니다.
import java.nio.charset.StandardCharsets;
// 서명 키를 생성할 때 사용되는 Key 인터페이스입니다.
import java.security.Key;
// 토큰 만료 기간 계산을 위해 Duration 클래스를 import 합니다.
import java.time.Duration;
// 날짜 및 시간 처리를 위한 Date 클래스를 import 합니다.
import java.util.Date;

// 스프링 빈으로 등록되어 다른 클래스에서 주입받아 사용할 수 있도록 합니다.
@Component
public class TokenProvider {

    // application.properties 또는 application.yml에 설정한 jwt.secret_key 값을 주입받습니다.
    @Value("${jwt.secret_key}")
    private String secretKey;

    // 토큰 생성 시 만료 기간을 매개변수로 받으므로, expireLength 프로퍼티는 필요에 따라 사용할 수 있습니다.
    // @Value("${jwt.expire-length}")
    // private long expireLength;

    // 비밀키 문자열을 바탕으로 JWT 토큰의 서명을 위한 Key 객체를 생성합니다.
    // 키 길이는 HS256 알고리즘 사용 시 최소 256비트여야 합니다.
    private Key getSigningKey() {
        // secretKey를 UTF-8 인코딩하여 바이트 배열로 변환한 후, HMAC SHA 키를 생성합니다.
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 주어진 memberId와 만료 기간을 사용하여 JWT 토큰을 생성합니다.
     *
     * @param memberId 회원 ID (문자열)
     * @param duration 토큰 만료 기간
     * @return JWT 토큰 문자열
     */
    public String generateToken(String memberId, Duration duration) {
        // 현재 시간을 Date 객체로 생성합니다.
        Date now = new Date();
        // 현재 시간(now)에 duration(밀리초 단위)을 더하여 토큰의 만료 시간을 계산합니다.
        Date expiryDate = new Date(now.getTime() + duration.toMillis());

        // JWT 빌더를 사용하여 토큰 생성 과정을 시작합니다.
        return Jwts.builder()
                // 토큰에 "memberId" 클레임을 추가하여 회원 식별 정보를 담습니다.
                .claim("memberId", memberId)
                // 토큰 발급 시간을 설정합니다.
                .setIssuedAt(now)
                // 토큰 만료 시간을 설정합니다.
                .setExpiration(expiryDate)
                // 서명 키와 HS256 알고리즘을 사용하여 토큰에 서명합니다.
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                // 최종적으로 compact() 메서드를 호출하여 JWT 토큰 문자열을 생성합니다.
                .compact();
    }

    /**
     * JWT 토큰에서 memberId를 추출합니다.
     *
     * @param token JWT 토큰
     * @return 토큰에 포함된 memberId
     * @throws RuntimeException 토큰이 유효하지 않을 경우 발생
     */
    public String extractMemberId(String token) {
        try {
            // JWT 파서 빌더를 생성하고 서명 키를 설정한 후, 토큰을 파싱하여 Claims(토큰에 담긴 정보)를 추출합니다.
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            // 추출된 Claims에서 "memberId" 값을 String 타입으로 반환합니다.
            return claims.get("memberId", String.class);
        } catch (JwtException e) {
            // 토큰 파싱 중 오류가 발생하면 RuntimeException을 발생시키며, "Invalid token" 메시지와 함께 원인을 포함합니다.
            throw new RuntimeException("Invalid token", e);
        }
    }
}
