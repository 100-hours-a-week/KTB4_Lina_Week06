package com.community.api.user;

import com.community.api.user.dto.UpdateProfileRequest;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;


import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static aQute.bnd.annotation.headers.Category.users;

@Repository
public class UserRepository {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String FILE_PATH = "src/main/resources/data/users.json";

    // json 파일 읽기
    private List<User> readAll() throws IOException {
        File file = new File(FILE_PATH);
        // System.out.println("파일 경로: " + file.getAbsolutePath());
        // System.out.println("파일 존재: " + file.exists());
        return objectMapper.readValue(file, new TypeReference<List<User>>() {});
    }

    // 이메일 일치하는 유저 찾기
    public Optional<User> findByEmail(String email) throws IOException{
        return readAll().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    // 닉네임 일치하는 유저 찾기
    public Optional<User> findByNickname(String nickname) throws IOException{
        return readAll().stream()
                .filter(user -> user.getNickname().equals(nickname))
                .findFirst();
    }

    // 유저 저장
    public User save(User user) throws IOException{
        List<User> users = readAll();
        Long newId = users.stream()
                        .mapToLong(User::getUserId)
                        .max()
                        .orElse(0L) + 1;
        user.setUserId(newId);
        users.add(user);
        File file = new File(FILE_PATH);
        objectMapper.writeValue(file, users);
        return user;
    }

    // userId로 유저 찾기
    public Optional<User> findByUserId(Long userId) throws IOException{
        return readAll().stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst();
    }

    // 업데이트 된 정보 저장
    public User update(User user) throws IOException{
        List<User> users = readAll();
        List<User> updated = users.stream()
                .map(u -> u.getUserId().equals(user.getUserId()) ? user : u)
                .collect(Collectors.toList());
        File file = new File(FILE_PATH);
        objectMapper.writeValue(file, updated);
        return user;
    }

    // 유저 정보 삭제(탈퇴)
    public void delete(Long userId) throws IOException {
        List<User> users = readAll();
        List<User> deleted = users.stream()
                .filter(u -> !u.getUserId().equals(userId))
                .collect(Collectors.toList());
        File file = new File(FILE_PATH);
        objectMapper.writeValue(file, deleted);
    }
}
