package com.community.api.user;

import com.community.api.auth.CustomUserDetails;
import com.community.api.common.ApiResponse;
import com.community.api.user.dto.LoginRequest;
import com.community.api.user.dto.SignupRequest;
import com.community.api.user.dto.UpdatePasswordRequest;
import com.community.api.user.dto.UpdateProfileRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<?>> signup(@RequestBody SignupRequest request) {
        Long userId = userService.signup(request);
        return ResponseEntity.status(201)
                .body(ApiResponse.of("register_success", Map.of("user_id", userId)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequest request, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        userService.login(request, httpServletRequest, httpServletResponse);
        return ResponseEntity.ok(ApiResponse.of("login_success"));
    }

    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<?>> updateProfile(@AuthenticationPrincipal CustomUserDetails principal, @RequestBody UpdateProfileRequest updateRequest) {
        userService.updateProfile(principal.getUserId(), updateRequest);
        return ResponseEntity.ok(ApiResponse.of("update_profile_success"));
    }

    @PatchMapping("/password")
    public ResponseEntity<ApiResponse<?>> updatePassword(@AuthenticationPrincipal CustomUserDetails principal, @RequestBody UpdatePasswordRequest updatePasswordRequest) {
        userService.updatePassword(principal.getUserId(), updatePasswordRequest);
        return ResponseEntity.ok(ApiResponse.of("update_password_success"));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<?>> delete(@AuthenticationPrincipal CustomUserDetails principal) {
        userService.deleteUser(principal.getUserId());
        return ResponseEntity.status(204).build();
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> getMyInfo(@AuthenticationPrincipal CustomUserDetails principal) {

        User user = userService.getMyInfo(principal.getUserId());
        Map<String, Object> data = Map.of(
                "email", user.getEmail(),
                "nickname", user.getNickname(),
                "profile_image", user.getProfileImage() == null ? "" : user.getProfileImage()
        );
        return ResponseEntity.status(200).body(ApiResponse.of("get_my_info_success", data));
    }
}
