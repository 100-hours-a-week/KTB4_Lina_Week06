package com.community.api.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 저장 및 Id 조회 가능")
    void test_saveAndFindById(){
        // 1. 준비 given
        User user = new User();
        user.setEmail("test1@gmail.com");
        user.setPassword("Test1234!");
        user.setNickname("test");
        user.setProfileImage("https://example.com/profile.jpg");

        User savedUser = userRepository.save(user);
        // 2. 실행 when
        Optional<User> foundUser = userRepository.findById(savedUser.getUserId());
        // 3. 검증 then
        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getUserId(), foundUser.get().getUserId());
        assertEquals("test1@gmail.com", foundUser.get().getEmail());
        assertEquals("test", foundUser.get().getNickname());
        assertEquals("https://example.com/profile.jpg", foundUser.get().getProfileImage());
    }

    @Test
    @DisplayName("이메일 사용자 조회")
    void test_findByEmail(){
        User user = new User();
        user.setEmail("test2@gmail.com");
        user.setPassword("Test12345!");
        user.setNickname("test2");
        user.setProfileImage("https://example.com/profile2.jpg");

        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmail("test2@gmail.com");

        assertTrue(foundUser.isPresent());
        assertEquals("test2@gmail.com", foundUser.get().getEmail());
        assertEquals("test2", foundUser.get().getNickname());
    }

    @Test
    @DisplayName("이메일 중복 확인")
    void test_findByEmail_duplicate(){
        User user = new User();
        user.setEmail("test3@gmail.com");
        user.setPassword("Test123456!");
        user.setNickname("test3");
        user.setProfileImage("https://example.com/profile3.jpg");

        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByEmail("test3@gmail.com");

        assertTrue(foundUser.isPresent());
        assertEquals("test3@gmail.com", foundUser.get().getEmail());
    }

    @Test
    @DisplayName("존재하지 않는 이메일 확인시 비어있음")
    void test_findByEmail_notFound(){
        String email = "notfoundEmail@gmail.com";

        Optional<User> foundUser = userRepository.findByEmail(email);

        assertTrue(foundUser.isEmpty());
    }

    @Test
    @DisplayName("닉네임으로 사용자 조회")
    void test_findByNickname(){
        User user = new User();
        user.setEmail("test4@gmail.com");
        user.setPassword("Test412345!");
        user.setNickname("test4");
        user.setProfileImage("https://example.com/profile4.jpg");

        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByNickname("test4");

        assertTrue(foundUser.isPresent());
        assertEquals("test4", foundUser.get().getNickname());
        assertEquals("test4@gmail.com", foundUser.get().getEmail());
    }

    @Test
    @DisplayName("닉네임 중복 확인")
    void test_findByNickname_duplicate(){
        User user = new User();
        user.setEmail("test5@gmail.com");
        user.setPassword("Test51234!");
        user.setNickname("test5");
        user.setProfileImage("https://example.com/profile5.jpg");

        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByNickname("test5");

        assertTrue(foundUser.isPresent());
        assertEquals("test5", foundUser.get().getNickname());
    }

    @Test
    @DisplayName("존재하지 않는 닉네임 확인시 비어있음")
    void test_findByNickname_notFound(){
        String nickname = "notfoundNickname";

        Optional<User> foundUser = userRepository.findByNickname(nickname);

        assertTrue(foundUser.isEmpty());
    }
}
