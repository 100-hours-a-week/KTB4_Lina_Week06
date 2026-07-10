package com.community.api.user;

import com.community.api.comment.CommentRepository;
import com.community.api.common.BadRequestException;
import com.community.api.common.DuplicateException;
import com.community.api.like.LikeRepository;
import com.community.api.post.Post;
import com.community.api.post.PostRepository;
import com.community.api.user.dto.SignupRequest;
import com.community.api.user.dto.UpdatePasswordRequest;
import com.community.api.user.dto.UpdateProfileRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
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

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private LikeRepository likeRepository;

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

    @Test
    @DisplayName("내 정보 조회 성공")
    void test_getMyInfo_success() {
        Long userId = 1L;

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("encodedPassword");
        user.setNickname("test");
        user.setProfileImage("https://example.com/profile.jpg");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        User result = userService.getMyInfo(userId);

        assertEquals("test@gmail.com", result.getEmail());
        assertEquals("test", result.getNickname());
        assertEquals("https://example.com/profile.jpg", result.getProfileImage());

        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("존재하지 않는 사용자이면 내 정보 조회 실패")
    void test_getMyInfo_fail_userNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> {
            userService.getMyInfo(userId);
        });

        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("프로필 수정에 성공한다")
    void test_updateProfile_success() {
        Long userId = 1L;

        UpdateProfileRequest request = new UpdateProfileRequest(
                "newNick",
                "https://example.com/new-profile.jpg"
        );

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("encodedPassword");
        user.setNickname("oldNickname");
        user.setProfileImage("https://example.com/old-profile.jpg");

        when(userRepository.findByNickname("newNick"))
                .thenReturn(Optional.empty());

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        userService.updateProfile(userId, request);

        assertEquals("newNick", user.getNickname());
        assertEquals("https://example.com/new-profile.jpg", user.getProfileImage());

        verify(userRepository).findByNickname("newNick");
        verify(userRepository).findById(userId);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("닉네임이 null이면 프로필 수정에 실패")
    void test_updateProfile_fail_nicknameRequired() {
        Long userId = 1L;

        UpdateProfileRequest request = new UpdateProfileRequest(
                "",
                "https://example.com/profile.jpg"
        );

        assertThrows(BadRequestException.class, () -> {
            userService.updateProfile(userId, request);
        });
    }

    @Test
    @DisplayName("닉네임 형식이 올바르지 않으면 프로필 수정에 실패")
    void test_updateProfile_fail_invalidNicknameFormat() {
        Long userId = 1L;

        UpdateProfileRequest request = new UpdateProfileRequest(
                "bad nickname",
                "https://example.com/profile.jpg"
        );

        assertThrows(BadRequestException.class, () -> {
            userService.updateProfile(userId, request);
        });
    }

    @Test
    @DisplayName("이미 존재하는 닉네임이면 프로필 수정에 실패")
    void test_updateProfile_fail_duplicateNickname() {
        Long userId = 1L;

        UpdateProfileRequest request = new UpdateProfileRequest(
                "testNick",
                "https://example.com/profile.jpg"
        );

        User existingUser = new User();
        ReflectionTestUtils.setField(existingUser, "userId", 2L);
        existingUser.setEmail("other@gmail.com");
        existingUser.setPassword("encodedPassword");
        existingUser.setNickname("testNick");
        existingUser.setProfileImage("https://example.com/other.jpg");

        when(userRepository.findByNickname("testNick"))
                .thenReturn(Optional.of(existingUser));

        assertThrows(DuplicateException.class, () -> {
            userService.updateProfile(userId, request);
        });

        verify(userRepository).findByNickname("testNick");
    }

    @Test
    @DisplayName("자기 자신의 닉네임이면 중복으로 보지 않고 프로필 수정에 성공")
    void test_updateProfile_success_sameUserNickname() {
        Long userId = 1L;

        UpdateProfileRequest request = new UpdateProfileRequest(
                "sameNick",
                "https://example.com/new-profile.jpg"
        );

        User user = new User();
        ReflectionTestUtils.setField(user, "userId", 1L);
        user.setEmail("test@gmail.com");
        user.setPassword("encodedPassword");
        user.setNickname("sameNick");
        user.setProfileImage("https://example.com/old-profile.jpg");

        when(userRepository.findByNickname("sameNick"))
                .thenReturn(Optional.of(user));

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        userService.updateProfile(userId, request);

        assertEquals("sameNick", user.getNickname());
        assertEquals("https://example.com/new-profile.jpg", user.getProfileImage());

        verify(userRepository).findByNickname("sameNick");
        verify(userRepository).findById(userId);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("존재하지 않는 사용자이면 프로필 수정에 실패")
    void test_updateProfile_fail_userNotFound() {
        Long userId = 1L;

        UpdateProfileRequest request = new UpdateProfileRequest(
                "newNick",
                "https://example.com/profile.jpg"
        );

        when(userRepository.findByNickname("newNick"))
                .thenReturn(Optional.empty());

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> {
            userService.updateProfile(userId, request);
        });

        verify(userRepository).findByNickname("newNick");
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("비밀번호 수정에 성공")
    void test_updatePassword_success() {
        Long userId = 1L;

        UpdatePasswordRequest request = new UpdatePasswordRequest(
                "Newpass123!"
        );

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("oldEncodedPassword");
        user.setNickname("test");
        user.setProfileImage("https://example.com/profile.jpg");

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode("Newpass123!"))
                .thenReturn("newEncodedPassword");

        userService.updatePassword(userId, request);

        assertEquals("newEncodedPassword", user.getPassword());

        verify(userRepository).findById(userId);
        verify(passwordEncoder).encode("Newpass123!");
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("비밀번호가 null이면 비밀번호 수정에 실패")
    void test_updatePassword_fail_passwordRequired() {

        Long userId = 1L;

        UpdatePasswordRequest request = new UpdatePasswordRequest(
                ""
        );

        assertThrows(BadRequestException.class, () -> {
            userService.updatePassword(userId, request);
        });
    }

    @Test
    @DisplayName("비밀번호 형식이 올바르지 않으면 비밀번호 수정에 실패")
    void test_updatePassword_fail_invalidPasswordFormat() {
        Long userId = 1L;

        UpdatePasswordRequest request = new UpdatePasswordRequest(
                "password"
        );

        assertThrows(BadRequestException.class, () -> {
            userService.updatePassword(userId, request);
        });
    }

    @Test
    @DisplayName("존재하지 않는 사용자이면 비밀번호 수정에 실패")
    void test_updatePassword_fail_userNotFound() {
        Long userId = 1L;

        UpdatePasswordRequest request = new UpdatePasswordRequest(
                "Newpass123!"
        );

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> {
            userService.updatePassword(userId, request);
        });

        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("회원탈퇴에 성공한다")
    void test_deleteUser_success() {
        // given
        Long userId = 1L;

        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("encodedPassword");
        user.setNickname("test");
        user.setProfileImage("https://example.com/profile.jpg");

        Post post = new Post();
        ReflectionTestUtils.setField(post, "postId", 10L);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(postRepository.findByAuthorId(userId))
                .thenReturn(List.of(post));

        // when
        userService.deleteUser(userId);

        // then
        verify(userRepository).findById(userId);
        verify(postRepository).findByAuthorId(userId);

        verify(commentRepository).deleteByPostId(10L);
        verify(likeRepository).deleteByPostId(10L);
        verify(postRepository).delete(post);

        verify(commentRepository).deleteByAuthorId(userId);
        verify(likeRepository).deleteByUserId(userId);
        verify(userRepository).delete(user);
    }

    @Test
    @DisplayName("존재하지 않는 사용자이면 회원탈퇴에 실패")
    void test_deleteUser_fail_userNotFound() {
        Long userId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> {
            userService.deleteUser(userId);
        });

        verify(userRepository).findById(userId);
    }
}