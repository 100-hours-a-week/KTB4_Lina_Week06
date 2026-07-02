package com.community.api.image;

// 프로필 이미지를 저장하기 위해 생성

import com.community.api.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/images")
public class ImageController {

    // 이미지 저장할 폴더
    private final String uploadDir = System.getProperty("user.home") + "/community-uploads/";

    @PostMapping
    public ResponseEntity<ApiResponse<?>> upload(@RequestParam("image")MultipartFile file) throws IOException{
        // 파일이 없을 경우
        if(file.isEmpty()){
            return ResponseEntity.status(400).body(ApiResponse.of("image_required"));
        }

        // 저장 폴더가 없을 경우
        File dir = new File(uploadDir);
        if(!dir.exists()) dir.mkdirs();

        // 파일명 겹치지 않도록 설정
        String original = file.getOriginalFilename();
        String ext = "";
        if (original != null && original.contains(".")){
            ext = original.substring(original.lastIndexOf("."));
        }
        String filename = UUID.randomUUID() + ext;

        // 폴더 저장
        file.transferTo(new File(uploadDir + filename));

        // URL 반환
        String url = "http://localhost:8080/images/" + filename;
        return ResponseEntity.status(201).body(ApiResponse.of("upload_success", Map.of("url", url)));
    }
}
