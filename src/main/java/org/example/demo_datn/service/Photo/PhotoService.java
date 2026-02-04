package org.example.demo_datn.service.Photo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.demo_datn.Enum.AlbumStatus;
import org.example.demo_datn.Dto.Response.Photo.PhotoResponse;
import org.example.demo_datn.service.api_tichhop.CloudService;
import org.springframework.security.core.Authentication;
import org.example.demo_datn.Entity.Album;
import org.example.demo_datn.Entity.Photo;
import org.example.demo_datn.Entity.User;
import org.example.demo_datn.Exception.AppException;
import org.example.demo_datn.Exception.ErrorCode;
import org.example.demo_datn.Repository.AlbumRepository;
import org.example.demo_datn.Repository.PermissionRepository;
import org.example.demo_datn.Repository.PhotoRepository;
import org.example.demo_datn.Repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PhotoService {

    private final PhotoRepository photoRepository;
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final CloudService cloudinaryService;
    private final PhotoFeedService photoFeedService;

    private PhotoResponse toResponse(Photo photo) {
        return PhotoResponse.builder()
                .id(photo.getId())
                .imageUrl(photo.getImageUrl())
                .width(photo.getWidth())
                .height(photo.getHeight())
                .size(photo.getSize())
                .albumId(photo.getAlbum().getId())
                .albumTitle(photo.getAlbum().getTitle())
                .ownerId(photo.getOwner().getId())
                .ownerUsername(photo.getOwner().getUsername())
                .build();
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assert auth != null;
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }
    /* ========================= UPLOAD ========================= */
    public List<PhotoResponse> uploadPhotos(List<MultipartFile> files, String albumId) {

        if (files == null || files.isEmpty()) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }

        User user = getCurrentUser();
        Album album = getAlbumWithPermissionCheck(albumId, user);

        List<PhotoResponse> responses = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                if (file.isEmpty() || !file.getContentType().startsWith("image/")) {
                    continue;
                }

                BufferedImage original = ImageIO.read(file.getInputStream());
                if (original == null) continue;

                BufferedImage resized = ImageUtil.resize(original, 1280);
                byte[] compressed = ImageUtil.compressJpeg(resized, 0.6f);

                String publicId = "photos/" + UUID.randomUUID();
                String imageUrl = cloudinaryService.uploadImage(compressed, publicId);

                Photo photo = new Photo();
                photo.setAlbum(album);
                photo.setOwner(user);
                photo.setImageUrl(imageUrl);
                photo.setWidth(resized.getWidth());
                photo.setHeight(resized.getHeight());
                photo.setSize(compressed.length);

                Photo saved = photoRepository.save(photo);
                responses.add(toResponse(saved));

            } catch (Exception e) {
                e.printStackTrace(); // không giết cả batch
            }
        }

        return responses;
    }


    /* ========================= QUERY ========================= */

//    public List<PhotoResponse> getPhotosEntityByAlbum(String albumId) {
//
//        User user = getCurrentUser();
//        album album = getAlbumWithPermissionCheck(albumId, user);
//
//        List<Photo> photos = photoRepository.findByAlbum(album);
//        List<PhotoResponse> responses = new ArrayList<>();
//
//        for (Photo photo : photos) {
//            responses.add(toResponse(photo));
//        }
//
//        return responses;
//    }

    public List<PhotoResponse> getPhotoFeedByAlbum(String albumId) {

        User user = getCurrentUser();
        Album album = getAlbumWithPermissionCheck(albumId, user);

        List<Photo> photos = photoRepository.findByAlbum(album);

        return photoFeedService.buildFeed(photos, user);
    }

    /* ========================= MOVE ========================= */

    public PhotoResponse movePhoto(String photoId, String newAlbumId) {

        User user = getCurrentUser();

        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new AppException(ErrorCode.PHOTO_NOT_FOUND));

        if (!photo.getOwner().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        Album newAlbum = albumRepository.findById(newAlbumId)
                .orElseThrow(() -> new AppException(ErrorCode.ALBUM_NOT_FOUND));

        photo.setAlbum(newAlbum);
        Photo saved = photoRepository.save(photo);

        return toResponse(saved);
    }

    /* ========================= DELETE ========================= */

    public void deletePhoto(String photoId) {

        User user = getCurrentUser();

        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new AppException(ErrorCode.PHOTO_NOT_FOUND));

        if (!photo.getAlbum().getOwner().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        cloudinaryService.deleteImage(photo.getImageUrl());
        photoRepository.delete(photo);
    }

    /* ========================= HELPERS ========================= */



    private Album getAlbumWithPermissionCheck(String albumId, User user) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new AppException(ErrorCode.ALBUM_NOT_FOUND));

        boolean isOwner = album.getOwner().getId().equals(user.getId());
        boolean hasPermission = permissionRepository.existsByUserAndAlbum(user, album);

        if (album.getStatus() == AlbumStatus.PRIVATE && !isOwner && !hasPermission) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        return album;
    }


}
