package org.example.demo_datn.Service;

import lombok.RequiredArgsConstructor;
import org.example.demo_datn.Dto.Enum.AlbumStatus;
import org.example.demo_datn.Dto.Request.Album.CreateAlbumRequest;
import org.example.demo_datn.Dto.Request.Album.UpdateAlbumRequest;
import org.example.demo_datn.Entity.*;
import org.example.demo_datn.Exception.AppException;
import org.example.demo_datn.Exception.ErrorCode;
import org.example.demo_datn.Repository.*;
import org.example.demo_datn.Service.Cloudinary.CloudinaryService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumService
{
    private final PermissionRepository permissionRepository;
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final CloudinaryService cloudinaryService;
    private final PhotoRepository photoRepository;


    public Album createAlbum(CreateAlbumRequest request) {

        // 1. Lấy user hiện tại
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // 2. Lấy location
        Locations location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new AppException(ErrorCode.LOCATION_NOT_FOUND));

        // 3. Tạo album
        Album album = new Album();
        album.setId(request.getId());
        album.setTitle(request.getTitle());
        album.setDescription(request.getDescription());
        album.setLocation(location);
        album.setOwner(user);
        album.setStatus(
                request.getStatus() != null
                        ? request.getStatus()
                        : AlbumStatus.PRIVATE
        );

        return albumRepository.save(album);
    }

    public List<Album> getMyAlbums() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        return albumRepository.findByOwner(user);
    }
    public Album getAlbumDetail(String albumId) {

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

        return album;
    }

    public Album updateAlbum(String albumId, UpdateAlbumRequest request) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new AppException(ErrorCode.ALBUM_NOT_FOUND));

        if (!album.getOwner().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        album.setTitle(request.getTitle());
        album.setDescription(request.getDescription());
        album.setStatus(request.getStatus());

        return albumRepository.save(album);
    }


    public void deleteAlbum(String albumId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new AppException(ErrorCode.ALBUM_NOT_FOUND));

        if (!album.getOwner().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        // 1. Lấy toàn bộ ảnh trong album
        List<Photo> photos = photoRepository.findByAlbum(album);

        // 2. Xóa ảnh trên Cloudinary
        for (Photo photo : photos) {
            cloudinaryService.deleteImage(photo.getImageUrl());
        }

        // 3. Xóa ảnh trong DB
        photoRepository.deleteAll(photos);

        // 4. Xóa album
        albumRepository.delete(album);
    }


}