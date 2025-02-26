package com.example.studysecurity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.security.auth.login.LoginException;
import java.util.Date;

@Component
public class JwtTokenGenerator {

    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.expire-length}")
    private long expireTimeMilliSecond;

    public String generateToken(final String id) {
        final Claims claims = Jwts.claims();
        claims.put("memberId", id);
        final Date now = new Date();
        final Date expiredDate = new Date(now.getTime() + expireTimeMilliSecond);

        // URL-safe Base64 인코딩을 위해 JWT의 compact() 메서드를 사용
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();  // URL-safe Base64로 토큰 생성
    }

    public String extractMemberId(final String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody()
                    .get("memberId")
                    .toString();
        } catch (final Exception error) {
            try {
                throw new LoginException("Invalid access token");
            } catch (LoginException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
