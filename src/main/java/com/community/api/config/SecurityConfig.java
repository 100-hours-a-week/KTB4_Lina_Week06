package com.community.api.config;

import com.community.api.auth.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity //메서드 보안 기능 활성화
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/error").permitAll()
                        .requestMatchers("/users/signup", "/users/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/posts/**", "/images/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/images").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().authenticated()
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                // 로그인(인증)하지 않고 보호된 api 접속 시도 시
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException)-> {
                            response.setStatus(401);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"message\":\"unauthorized\",\"data\":null}");
                        })
                )
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();

        // 허용할 출처 설정
        configuration.setAllowedOrigins(List.of("http://127.0.0.1:5500", "http://localhost:5500"));
        // 허용할 HTTP 메서드 설정
        configuration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS","PATCH"));
        // 허용할 HTTP 헤더 설정
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        // 브라우저에 노출할 헤더 설정

        // 자격 증명 허용 여부
        configuration.setAllowCredentials(true);
        // 예비요청 결과 캐시 시간 설정
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로에 대해 위에서 정의한 CORS 정책 활용
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration){
        return configuration.getAuthenticationManager();
    }

    public SecurityConfig(JwtFilter jwtFilter){
        this.jwtFilter = jwtFilter;
    }
}
