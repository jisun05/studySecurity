package com.example.studysecurity.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Date;

@Component
public class TokenProvider {

    @Value("${jwt.secret_key}")
    private String secretKey;

    // 토큰 생성 시 만료 기간을 매개변수로 받으므로, expireLength 프로퍼티는 필요에 따라 사용할 수 있습니다.
    // @Value("${jwt.expire-length}")
    // private long expireLength;

    // 비밀키를 기반으로 서명 키 생성 (키 길이는 최소 256비트 이상이어야 합니다)
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 주어진 memberId와 만료 기간을 사용하여 JWT 토큰 생성
     *
     * @param memberId 회원 ID (문자열)
     * @param duration 토큰 만료 기간
     * @return JWT 토큰 문자열
     */
    public String generateToken(String memberId, Duration duration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + duration.toMillis());

        return Jwts.builder()
                .claim("memberId", memberId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰에서 memberId를 추출
     *
     * @param token JWT 토큰
     * @return 토큰에 포함된 memberId
     * @throws RuntimeException 토큰이 유효하지 않을 경우
     */
    public String extractMemberId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("memberId", String.class);
        } catch (JwtException e) {
            throw new RuntimeException("Invalid token", e);
        }
    }
}
