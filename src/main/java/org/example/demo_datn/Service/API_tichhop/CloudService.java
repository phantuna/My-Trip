package org.example.demo_datn.Service.API_tichhop;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudService {

    private final Cloudinary cloudinary;

    public String uploadImage(byte[] imageBytes, String publicId) {
        try {
            Map uploadResult = cloudinary.uploader().upload(
                    imageBytes,
                    ObjectUtils.asMap(
                            "public_id", publicId,
                            "folder", "photos",
                            "resource_type", "image"
                    )
            );
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Upload to Cloudinary failed", e);
        }
    }
    public static String extractPublicId(String imageUrl) {
        try {
            String path = URI.create(imageUrl).getPath();
            // path ví dụ: /dlouzb28c/image/upload/v1767864436/photos/xxx.jpg

            String marker = "/upload/";
            int idx = path.indexOf(marker);
            if (idx < 0) throw new IllegalArgumentException("Invalid Cloudinary URL");

            String afterUpload = path.substring(idx + marker.length());
            // afterUpload: v1767864436/photos/xxx.jpg

            // bỏ "v123456/" nếu có
            if (afterUpload.startsWith("v")) {
                int slash = afterUpload.indexOf('/');
                if (slash > 0) afterUpload = afterUpload.substring(slash + 1);
                // afterUpload: photos/xxx.jpg
            }

            // bỏ extension
            int dot = afterUpload.lastIndexOf('.');
            if (dot > 0) afterUpload = afterUpload.substring(0, dot);

            return afterUpload; // photos/xxx
        } catch (Exception e) {
            throw new RuntimeException("Cannot extract public_id from url: " + imageUrl, e);
        }
    }

    public void deleteImage(String imageUrl) {
        try {
            String publicId = extractPublicId(imageUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));
        } catch (IOException e) {
            throw new RuntimeException("Delete from Cloudinary failed", e);
        }
    }

}
