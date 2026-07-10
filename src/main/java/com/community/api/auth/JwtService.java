package com.community.api.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final long accessTokenExpiration;

    public JwtService(JwtDecoder jwtDecoder, JwtEncoder jwtEncoder, @Value("${jwt.access-token-expiration}") long accessTokenExpiration){
        this.accessTokenExpiration = accessTokenExpiration;
        this.jwtDecoder = jwtDecoder;
        this.jwtEncoder = jwtEncoder;
    }

    public String createAccessToken(Long userId, String email){
        // 현재 시간 구하기
        Instant now = Instant.now();
        // 만료 시간 구하기
        Instant expiresAt = now.plusMillis(accessTokenExpiration);
        // JWT에 담을 정보 만들기
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(email)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .claim("userId", userId)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        // jwtEncoder로 토큰 문자열 만들기
        return jwtEncoder
                .encode(JwtEncoderParameters.from(header, claims))
                .getTokenValue();
    }

    public boolean isValidToken(String token){
        try {
            jwtDecoder.decode(token);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    public Long getUserId(String token){
        Jwt jwt = jwtDecoder.decode(token);
        Number userId = jwt.getClaim("userId");
        return userId.longValue();
    }


}
