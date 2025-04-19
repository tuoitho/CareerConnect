package com.careerconnect.service;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class ImageService {
    // Đường dẫn thư mục lưu ảnh, bạn có thể cấu hình đường dẫn này trong file application.properties
//    @Value("${image.upload-dir}")
//    private String uploadDir;
    @Autowired
    private Cloudinary cloudinary;

    public String uploadCloudinary(MultipartFile file) {
        try {
            var uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of());
            return uploadResult.get("url") != null ? (String)uploadResult.get("url") : null;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image to Cloudinary", e);
        }
    }


    // Hàm lấy đuôi file (ví dụ: jpg, png, ...)
    private String getFileExtension(String filename) {
        String extension = "";
        int i = filename.lastIndexOf('.');
        if (i > 0) {
            extension = filename.substring(i + 1);
        }
        return extension;
    }
}