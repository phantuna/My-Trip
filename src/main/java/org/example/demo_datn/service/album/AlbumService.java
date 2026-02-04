package org.example.demo_datn.service.album;

import lombok.RequiredArgsConstructor;
import org.example.demo_datn.Enum.AlbumStatus;
import org.example.demo_datn.Dto.Request.Album.CreateAlbumRequest;
import org.example.demo_datn.Dto.Request.Album.UpdateAlbumRequest;
import org.example.demo_datn.Dto.Response.Album.AlbumResponse;
import org.example.demo_datn.Entity.*;
import org.example.demo_datn.Exception.AppException;
import org.example.demo_datn.Exception.ErrorCode;
import org.example.demo_datn.Repository.*;
import org.example.demo_datn.service.api_tichhop.CloudService;
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
    private final CloudService cloudinaryService;
    private final PhotoRepository photoRepository;
    private final AlbumFeedService albumFeedService;


    private AlbumResponse toResponse(Album album) {
        return AlbumResponse.builder()
                .id(album.getId())
                .title(album.getTitle())
                .description(album.getDescription())
                .status(album.getStatus())
                .locationId(album.getLocation() != null ? album.getLocation().getId() : null)
                .locationName(album.getLocation() != null ? album.getLocation().getName() : null)
                .ownerId(album.getOwner() != null ? album.getOwner().getId() : null)
                .ownerUsername(album.getOwner() != null ? album.getOwner().getUsername() : null)
                .build();
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assert auth != null;
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public AlbumResponse createAlbum(CreateAlbumRequest request) {

        // 1. Lấy user hiện tại
        User username = getCurrentUser();
        // 2. Lấy location
        Locations location = locationRepository.findById(request.getLocationId())
                .orElseThrow(() -> new AppException(ErrorCode.LOCATION_NOT_FOUND));

        // 3. Tạo album
        Album album = new Album();
        album.setId(request.getId());
        album.setTitle(request.getTitle());
        album.setDescription(request.getDescription());
        album.setLocation(location);
        album.setOwner(username);
        album.setStatus(
                request.getStatus() != null
                        ? request.getStatus()
                        : AlbumStatus.PRIVATE
        );

        Album saved = albumRepository.save(album);
        return toResponse(saved);

    }

    public List<AlbumResponse> getMyAlbums() {
        User user = getCurrentUser();
        List<Album> albums = albumRepository.findByOwner(user);

        return albumFeedService.buildFeed(albums);
    }

    public AlbumResponse getAlbumDetail(String albumId) {

        User username = getCurrentUser();
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new AppException(ErrorCode.ALBUM_NOT_FOUND));

        boolean isOwner = album.getOwner().getId().equals(username.getId());
        boolean hasPermission = permissionRepository.existsByUserAndAlbum(username, album);

        if (album.getStatus() == AlbumStatus.PRIVATE && !isOwner && !hasPermission) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        return toResponse(albumRepository.save(album));

    }

    public AlbumResponse updateAlbum(String albumId, UpdateAlbumRequest request) {

        User username = getCurrentUser();
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new AppException(ErrorCode.ALBUM_NOT_FOUND));

        if (!album.getOwner().getId().equals(username.getId())) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        album.setTitle(request.getTitle());
        album.setDescription(request.getDescription());
        album.setStatus(request.getStatus());

        return toResponse(albumRepository.save(album));

    }


    public void deleteAlbum(String albumId) {

        User username = getCurrentUser();
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new AppException(ErrorCode.ALBUM_NOT_FOUND));

        if (!album.getOwner().getId().equals(username.getId())) {
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

    private AlbumResponse getAlbumWithPermissionCheck(String albumId, User user) {

        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new AppException(ErrorCode.ALBUM_NOT_FOUND));

        boolean isOwner = album.getOwner().getId().equals(user.getId());
        boolean hasPermission = permissionRepository.existsByUserAndAlbum(user, album);

        if (album.getStatus() == AlbumStatus.PRIVATE && !isOwner && !hasPermission) {
            throw new AppException(ErrorCode.ACCESS_DENIED);
        }

        Album saved = albumRepository.save(album) ;

        return toResponse(saved);

    }


    public List<AlbumResponse> searchByTitle(String keyword) {
        return albumRepository
                .findByStatusAndTitleContainingIgnoreCase(
                        AlbumStatus.PUBLIC, // hoặc APPROVAL
                        keyword
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<AlbumResponse> searchByLocation(String locationName) {
        return albumRepository
                .searchByLocationName( locationName)
                .stream()
                .map(this::toResponse)
                .toList();
    }
    public List<AlbumResponse> findNearby(Double lat, Double lng) {
        double delta = 0.01;

        return albumRepository
                .findNearby(
                        AlbumStatus.PUBLIC,
                        lat - delta,
                        lat + delta,
                        lng - delta,
                        lng + delta
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

}