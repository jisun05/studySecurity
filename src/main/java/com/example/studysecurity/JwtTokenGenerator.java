package com.example.studysecurity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenGenerator {

    @Value("${jwt.secret_key}") // application.yml에서 secret_key 가져오기
    private String secretKey;

    @Value("${jwt.expire-length}")
    private long expireTimeMilliSecond;

    public String generateToken(final String id) {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException("jwt.secret_key가 application.yml에 설정되지 않았습니다.");
        }

        final Claims claims = Jwts.claims();
        claims.put("memberId", id);
        final Date now = new Date();
        final Date expiredDate = new Date(now.getTime() + expireTimeMilliSecond);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey)), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractMemberId(final String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))) // 수정된 부분
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("memberId", String.class);
        } catch (final Exception error) {
            throw new RuntimeException("Invalid access token", error);
        }
    }
}
