package org.example.demo_datn.Controller;

import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<Photo> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam String albumId
    ) throws IOException {
        return ResponseEntity.ok(photoService.uploadPhoto(file, albumId));
    }

    @GetMapping("/album/{albumId}")
    public ResponseEntity<List<Photo>> getByAlbum(@PathVariable String albumId) {
        return ResponseEntity.ok(photoService.getPhotosByAlbum(albumId));
    }


    @DeleteMapping("/{photoId}")
    public ResponseEntity<Void> delete(@PathVariable String photoId) {
        photoService.deletePhoto(photoId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/move/{photoId}")
    public ResponseEntity<Photo> move(
            @PathVariable String photoId,
            @RequestParam String albumId
    ) {
        return ResponseEntity.ok(photoService.movePhoto(photoId, albumId));
    }
}
