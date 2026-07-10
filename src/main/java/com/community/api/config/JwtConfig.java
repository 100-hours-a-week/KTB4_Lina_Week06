package com.community.api.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class JwtConfig {

    private final String secret;

    // secret 값을 받는다
    public JwtConfig(@Value("${jwt.secret}") String secret){
        this.secret = secret;
    }

    // SecretKey를 만든다
    @Bean
    public SecretKey secretKey(){
        return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }
    // JwtEncoder 만들기
    @Bean
    public JwtEncoder jwtEncoder(SecretKey secretKey){
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));
    }
    // JwtDecoder 만들기
    @Bean
    public JwtDecoder jwtDecoder(SecretKey secretKey){
        return NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}

