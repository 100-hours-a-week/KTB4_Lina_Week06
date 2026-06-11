package com.community.api.common;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenStore {
    private final Map<String, Long> tokenMap = new ConcurrentHashMap<>();

    public String createToken(Long userId){
        String token = UUID.randomUUID().toString();
        tokenMap.put(token, userId);
        return token;
    }

    public Long getUserId(String token){
        return tokenMap.get(token);
    }
}