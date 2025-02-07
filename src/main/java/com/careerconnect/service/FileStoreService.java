package com.careerconnect.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor //For constructor injection
@Slf4j
public class FileStoreService {

    private final Cloudinary cloudinary;  // Inject Cloudinary instance

    public String uploadPdf(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload an empty file");
        }

        if (!"application/pdf".equals(file.getContentType())) {  //Check content Type
            throw new IllegalArgumentException("Only PDF files are allowed");
        }

        try {
            // Generate a unique public ID using UUID
            String publicId = UUID.randomUUID().toString();

            // Upload the file to Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "resource_type", "auto"  // Let Cloudinary detect the resource type
                            // "folder", cloudinaryFolder,  // No need now, included in publicId
                            // You can add other options here, like transformations
                    )
            );

            // Get the secure URL of the uploaded file
            return (String) uploadResult.get("secure_url");

        } catch (IOException e) {
            log.error("Error uploading file to Cloudinary", e);
            throw new RuntimeException("Failed to upload file to Cloudinary", e); //Re-throw for handling
        }
    }

    public void deleteFile(String publicId) {
        try {
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Cloudinary deletion result: {}", result);
        } catch (IOException e) {
            log.error("Error deleting file from Cloudinary", e);
            throw new RuntimeException("Failed to delete file from Cloudinary.", e);
        }
    }

    //helper function to get the publicId
    public String extractPublicIdFromUrl(String secureUrl) {
        // Example URL: https://res.cloudinary.com/your_cloud_name/image/upload/v1678886400/careerconnect/example.jpg
        if(secureUrl == null || secureUrl.isEmpty()) {
            return null; // Or throw an exception, if appropriate.
        }
        int lastSlashIndex = secureUrl.lastIndexOf("/");
        if (lastSlashIndex == -1) {
            return null; // Invalid format
        }
        //get file name
        String fileNameWithExtension = secureUrl.substring(lastSlashIndex + 1);

        int lastDotIndex = fileNameWithExtension.lastIndexOf(".");
        if(lastDotIndex == -1){
            return null;
        }
        //get file name without extension
        String fileNameWithOutExtension = fileNameWithExtension.substring(0,lastDotIndex);


        String folderPath = fileNameWithOutExtension;

        return folderPath;
    }
}