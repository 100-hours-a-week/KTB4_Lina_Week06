package com.community.api.interceptor;

import ch.qos.logback.core.subst.Token;
import com.community.api.common.BadRequestException;
import com.community.api.common.TokenStore;
import com.community.api.user.dto.LoginRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final TokenStore tokenStore;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception{

        // GET 요청 인증 불필요
        if(request.getMethod().equals("GET") || request.getMethod().equals("OPTIONS")){
            return true;
        }
        String authHeader = request.getHeader("Authorization");
        // 헤더 없을 시 401
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"unauthorized\",\"data\":null}");
            return false;
        }

        // 토큰으로 userid 조회
        String token = authHeader.substring(7);

        Long userId = tokenStore.getUserId(token);

        // userId 없을 시 401
        if (userId == null){
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"message\":\"unauthorized\",\"data\":null}");
            return false;
        }

        // 저장
        request.setAttribute("userId", userId);
        return true;

    }
}
