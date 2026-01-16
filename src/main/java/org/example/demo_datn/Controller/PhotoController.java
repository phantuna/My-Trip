package org.example.demo_datn.Controller;

import lombok.RequiredArgsConstructor;
import org.example.demo_datn.Dto.Response.Photo.PhotoResponse;
import org.example.demo_datn.Entity.Photo;
import org.example.demo_datn.Service.Photo.PhotoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;

    @PostMapping("/upload")
    public ResponseEntity<List<PhotoResponse>> upload(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam String albumId
    ) {
        return ResponseEntity.ok(photoService.uploadPhotos(files, albumId));
    }


    @GetMapping("/album/{albumId}")
    public ResponseEntity<List<PhotoResponse>> getByAlbum(@PathVariable String albumId) {
        return ResponseEntity.ok(photoService.getPhotosByAlbum(albumId));
    }


    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> delete(@PathVariable String photoId) {
        photoService.deletePhoto(photoId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/move/{photoId}")
    public ResponseEntity<PhotoResponse> move(
            @PathVariable String photoId,
            @RequestParam String albumId
    ) {
        return ResponseEntity.ok(photoService.movePhoto(photoId, albumId));
    }
}
