package org.example.demo_datn.Service.Photo;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.demo_datn.Dto.Enum.AlbumStatus;
import org.example.demo_datn.Service.Cloudinary.CloudinaryService;
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
import java.io.File;
import java.io.IOException;
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
    private final CloudinaryService cloudinaryService;

    public Photo uploadPhoto(MultipartFile file, String albumId) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }

        if (!file.getContentType().startsWith("image/")) {
            throw new AppException(ErrorCode.FILE_NOT_IMAGE);
        }

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new AppException(ErrorCode.ALBUM_NOT_FOUND));

        boolean isOwner = album.getOwner().getId().equals(user.getId());
        boolean hasPermission = permissionRepository.existsByUserAndAlbum(user, album);

        if (!isOwner && !hasPermission) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        if (album.getStatus() == AlbumStatus.PRIVATE && !isOwner) {
            throw new AppException(ErrorCode.ALBUM_PRIVATE);
        }

        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            throw new AppException(ErrorCode.FILE_INVALID);
        }

        BufferedImage resizedImage = ImageUtil.resize(originalImage, 1280);
        byte[] compressedBytes = ImageUtil.compressJpeg(resizedImage, 0.6f);

        String imageUrl = cloudinaryService.uploadImage(
                compressedBytes,
                UUID.randomUUID().toString()
        );

        Photo photo = new Photo();
        photo.setAlbum(album);
        photo.setOwner(user);
        photo.setImageUrl(imageUrl);
        photo.setWidth(resizedImage.getWidth());
        photo.setHeight(resizedImage.getHeight());
        photo.setSize(compressedBytes.length);

        return photoRepository.save(photo);
    }

    public List<Photo> getPhotosByAlbum(String albumId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new AppException(ErrorCode.ALBUM_NOT_FOUND));

        boolean isOwner = album.getOwner().getId().equals(user.getId());
        boolean hasPermission = permissionRepository.existsByUserAndAlbum(user, album);

        if (album.getStatus() == AlbumStatus.PRIVATE && !isOwner && !hasPermission) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        return photoRepository.findByAlbum(album);
    }

    public Photo movePhoto(String photoId, String newAlbumId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new AppException(ErrorCode.PHOTO_NOT_FOUND));

        Album newAlbum = albumRepository.findById(newAlbumId)
                .orElseThrow(() -> new AppException(ErrorCode.ALBUM_NOT_FOUND));

        if (!photo.getOwner().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        photo.setAlbum(newAlbum);
        return photoRepository.save(photo);
    }


    public void deletePhoto(String photoId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Photo photo = photoRepository.findById(photoId)
                .orElseThrow(() -> new AppException(ErrorCode.PHOTO_NOT_FOUND));

        Album album = photo.getAlbum();

        boolean isOwner = album.getOwner().getId().equals(user.getId());

        if (!isOwner) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        // xóa trên Cloudinary
        cloudinaryService.deleteImage(photo.getImageUrl());

        photoRepository.delete(photo);
    }



}
