package com.community.api.user;

import com.community.api.user.dto.SignupRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원가입 성공")
    void test_signup_success() {
        // given
        SignupRequest request = new SignupRequest(
                "test@gmail.com",
                "Test1234!",
                "test",
                "https://example.com/profile.jpg"
        );

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.empty());

        when(userRepository.findByNickname("test"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("Test1234!"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        userService.signup(request);

        // then
        verify(userRepository).findByEmail("test@gmail.com");
        verify(userRepository).findByNickname("test");
        verify(passwordEncoder).encode("Test1234!");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("이미 존재하는 이메일이면 회원가입에 실패한다")
    void test_signup_fail_duplicateEmail() {
        // given
        SignupRequest request = new SignupRequest(
                "test@gmail.com",
                "Test1234!",
                "test",
                "https://example.com/profile.jpg"
        );

        User existingUser = new User();
        existingUser.setEmail("test@gmail.com");
        existingUser.setPassword("encodedPassword");
        existingUser.setNickname("oldUser");
        existingUser.setProfileImage("https://example.com/old.jpg");

        when(userRepository.findByEmail("test@gmail.com"))
                .thenReturn(Optional.of(existingUser));

        // when & then
        assertThrows(RuntimeException.class, () -> {
            userService.signup(request);
        });

        verify(userRepository).findByEmail("test@gmail.com");
    }

    @Test
    @DisplayName("이미 존재하는 닉네임이면 회원가입에 실패한다")
    void test_signup_fail_duplicateNickname() {
        // given
        SignupRequest request = new SignupRequest(
                "test2@gmail.com",
                "Test21234!",
                "test2",
                "https://example.com/profile2.jpg"
        );

        User existingUser = new User();
        existingUser.setEmail("old@gmail.com");
        existingUser.setPassword("encodedPassword");
        existingUser.setNickname("test2");
        existingUser.setProfileImage("https://example.com/old.jpg");

        when(userRepository.findByEmail("test2@gmail.com"))
                .thenReturn(Optional.empty());

        when(userRepository.findByNickname("test2"))
                .thenReturn(Optional.of(existingUser));

        assertThrows(RuntimeException.class, () -> {
            userService.signup(request);
        });

        verify(userRepository).findByEmail("test2@gmail.com");
        verify(userRepository).findByNickname("test2");
    }

    @Test
    @DisplayName("회원가입 시 비밀번호 암호화")
    void test_signup_encodedPassword(){
        SignupRequest request = new SignupRequest(
                "test3@gmail.com",
                "Test31234!",
                "test3",
                "https://example.com/profile3.jpg"
        );

        when(userRepository.findByEmail("test3@gmail.com"))
                .thenReturn(Optional.empty());
        when(userRepository.findByNickname("test3"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("Test31234!"))
                .thenReturn("encodedPassword");
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        userService.signup(request);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userArgumentCaptor.capture());

        User savedUser = userArgumentCaptor.getValue();

        assertEquals("encodedPassword", savedUser.getPassword());
    }

    @Test
    @DisplayName("회원가입 시 요청 값이 사용자 정보로 저장")
    void test_signup_saveUserInfo() {
        SignupRequest request = new SignupRequest(
                "test4@gmail.com",
                "Test41234!",
                "test4",
                "https://example.com/profile4.jpg"
        );

        when(userRepository.findByEmail("test4@gmail.com"))
                .thenReturn(Optional.empty());

        when(userRepository.findByNickname("test4"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("Test41234!"))
                .thenReturn("encodedPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        userService.signup(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals("test4@gmail.com", savedUser.getEmail());
        assertEquals("test4", savedUser.getNickname());
        assertEquals("https://example.com/profile4.jpg", savedUser.getProfileImage());
    }
}