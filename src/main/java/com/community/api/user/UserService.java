package com.community.api.user;

import com.community.api.comment.CommentRepository;
import com.community.api.common.BadRequestException;
import com.community.api.common.DuplicateException;
import com.community.api.common.TokenStore;
import com.community.api.like.LikeRepository;
import com.community.api.post.Post;
import com.community.api.post.PostRepository;
import com.community.api.user.dto.LoginRequest;
import com.community.api.user.dto.SignupRequest;
import com.community.api.user.dto.UpdatePasswordRequest;
import com.community.api.user.dto.UpdateProfileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final TokenStore tokenStore;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    public Long signup(SignupRequest request) {

        // 이메일 필수 기입
        if (request.getEmail() == null || request.getEmail().isBlank()){
            throw new BadRequestException("email_required");
        }

        // 이메일 형식 확인
        if (!request.getEmail().matches("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$")){
            throw new BadRequestException("invalid_email_format");
        }

        // 비밀번호 필수 기입
        if (request.getPassword() == null || request.getPassword().isBlank()){
            throw new BadRequestException("password_required");
        }

        // 비밀번호 형식 확인
        if (!request.getPassword().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=]).{8,20}$")) {
            throw new BadRequestException("invalid_password_format");
        }

        // 닉네임 필수 기입
        if (request.getNickname() == null || request.getNickname().isBlank()){
            throw new BadRequestException("nickname_required");
        }

        // 닉네임 형식 확인
        if (request.getNickname().length() > 10 || request.getNickname().contains(" ")){
            throw new BadRequestException("invalid_nickname_format");
        }

        // 프로필 사진 필수 기입
        if (request.getProfileImage() == null || request.getProfileImage().isBlank()){
            throw new BadRequestException("profile_image_required");
        }

        // 이미 존재하는 이메일
        if (userRepository.findByEmail(request.getEmail()).isPresent()){
            throw new DuplicateException("duplicate_email");
        }

        // 이미 존재하는 닉네임
        if (userRepository.findByNickname(request.getNickname()).isPresent()){
            throw new DuplicateException("duplicate_nickname");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setNickname(request.getNickname());
        user.setProfileImage(request.getProfileImage());

        User savedUser = userRepository.save(user);
        return savedUser.getUserId();
    }

    public String login(LoginRequest request) {
        // 이메일 필수 기입
        if (request.getEmail() == null || request.getEmail().isBlank()){
            throw new BadRequestException("email_required");
        }
        // 이메일 형식 확인
        if (!request.getEmail().matches("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$")){
            throw new BadRequestException("invalid_email_format");
        }
        // 비밀번호 필수 기입
        if (request.getPassword() == null || request.getPassword().isBlank()){
            throw new BadRequestException("password_required");
        }
        // 비밀번호 형식 확인
        if (!request.getPassword().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=]).{8,20}$")) {
            throw new BadRequestException("invalid_password_format");
        }
        // 이메일 혹은 비밀번호 오류
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("invalid_credentials"));
        if (!user.getPassword().equals(request.getPassword())){
            throw new BadRequestException("invalid_credentials");
        }

        return tokenStore.createToken(user.getUserId());
    }

    // 업데이트 프로필
    public void updateProfile(Long userId, UpdateProfileRequest request) {
        // 닉네임 필수 기입
        if (request.getNickname() == null || request.getNickname().isBlank()){
            throw new BadRequestException("nickname_required");
        }
        // 닉네임 형식 확인
        if (request.getNickname().length()>10 || request.getNickname().contains(" ")){
            throw new BadRequestException("invalid_nickname_format");
        }
        // 닉네임 중복 체크-> 자기 자신 제외
        userRepository.findByNickname(request.getNickname())
                .ifPresent(existing -> {
                    if (!existing.getUserId().equals(userId)) {
                        throw new DuplicateException("duplicate_nickname");
                    }
                });

        // userid 찾기 -> 서비스 요청한 것이 본인인지 확인하기 위함
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("invalid_credentials"));

        // 유저 정보 수정
        user.setNickname(request.getNickname());
        if(request.getProfileImage() != null){
            user.setProfileImage(request.getProfileImage());
        }
        userRepository.save(user);
    }

    // 비밀번호 수정
    public void updatePassword(Long userId, UpdatePasswordRequest request){
        // 비밀번호 필수 기입
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BadRequestException("password_required");
        }
        // 비밀번호 형식 확인
        if (!request.getPassword().matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=]).{8,20}$")) {
            throw new BadRequestException("invalid_password_format");
        }
        // userid 찾기 -> 서비스 요청한 것이 본인인지 확인하기 위함
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("invalid_credentials"));

        // 수정 정보 저장
        user.setPassword(request.getPassword());
        userRepository.save(user);
    }

    // 회원탈퇴
    @Transactional
    public void deleteUser(Long userId){
        // userid 찾기 -> 서비스 요청한 것이 본인인지 확인하기 위함
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("invalid_credentials"));
        // 회원이 쓴 게시글의 댓글과 좋아요 삭제 -> 게시글 삭제
        List<Post> myPosts = postRepository.findByAuthorId(userId);
        for (Post post : myPosts){
            commentRepository.deleteByPostId(post.getPostId());
            likeRepository.deleteByPostId(post.getPostId());
            postRepository.delete(post);
        }
        // 회원이 남긴 댓글 삭제
        commentRepository.deleteByAuthorId(userId);

        // 회원이 남긴 좋아요 삭제
        likeRepository.deleteByUserId(userId);

        // 탈퇴 처리
        userRepository.delete(user);
    }

    // 내 정보 조회
    public User getMyInfo(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("invalid_credentials"));
    }
}
