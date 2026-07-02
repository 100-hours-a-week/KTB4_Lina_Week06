package com.community.api.user;

import com.community.api.common.ApiResponse;
import com.community.api.common.TokenStore;
import com.community.api.user.dto.LoginRequest;
import com.community.api.user.dto.SignupRequest;
import com.community.api.user.dto.UpdatePasswordRequest;
import com.community.api.user.dto.UpdateProfileRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final TokenStore tokenStore;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<?>> signup(@RequestBody SignupRequest request) throws Exception{
        Long userId = userService.signup(request);
        return ResponseEntity.status(201)
                .body(ApiResponse.of("register_success", Map.of("user_id", userId)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequest request) throws Exception{
        String token = userService.login(request);
        return ResponseEntity.status(200)
                .body(ApiResponse.of("login_success", Map.of("token", token)));
    }

    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<?>> updateProfile(HttpServletRequest httpRequest, @RequestBody UpdateProfileRequest updateRequest) throws Exception {
        Long userId = (Long) httpRequest.getAttribute("userId");
        userService.updateProfile(userId, updateRequest);
        return ResponseEntity.status(200)
                .body(ApiResponse.of("update_profile_success"));
    }

    @PatchMapping("/password")
    public ResponseEntity<ApiResponse<?>> updatePassword(HttpServletRequest httpRequest, @RequestBody UpdatePasswordRequest updatePasswordRequest) throws Exception{
        Long userId = (Long) httpRequest.getAttribute("userId");
        userService.updatePassword(userId, updatePasswordRequest);
        return ResponseEntity.status(200)
                .body(ApiResponse.of("update_password_success"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<?>> delete(HttpServletRequest httpRequest) throws Exception{
        Long userId = (Long) httpRequest.getAttribute("userId");
        userService.deleteUser(userId);
        return ResponseEntity.status(204).build();
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> getMyInfo(HttpServletRequest httpRequest) throws Exception{
        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            return ResponseEntity.status(401).body(ApiResponse.of("unauthorized"));
        }
        Long userId = tokenStore.getUserId(authHeader.substring(7));
        if (userId == null){
            return ResponseEntity.status(401).body(ApiResponse.of("unauthorized"));
        }

        User user = userService.getMyInfo(userId);
        Map<String, Object> data = Map.of(
                "email", user.getEmail(),
                "nickname", user.getNickname(),
                "profile_image", user.getProfileImage() == null ? "" : user.getProfileImage()
        );
        return ResponseEntity.status(200).body(ApiResponse.of("get_my_info_success", data));
    }
}
