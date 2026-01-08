package org.example.demo_datn.Controller;


import lombok.RequiredArgsConstructor;
import org.example.demo_datn.Dto.Request.Album.CreateAlbumRequest;
import org.example.demo_datn.Dto.Request.Album.UpdateAlbumRequest;
import org.example.demo_datn.Entity.Album;
import org.example.demo_datn.Service.AlbumService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;

    @PostMapping
    public ResponseEntity<Album> create(@RequestBody CreateAlbumRequest request) {
        return ResponseEntity.ok(albumService.createAlbum(request));
    }

    @GetMapping("/me")
    public ResponseEntity<List<Album>> myAlbums() {
        return ResponseEntity.ok(albumService.getMyAlbums());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Album> detail(@PathVariable String id) {
        return ResponseEntity.ok(albumService.getAlbumDetail(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Album> update(
            @PathVariable String id,
            @RequestBody UpdateAlbumRequest request
    ) {
        return ResponseEntity.ok(albumService.updateAlbum(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        albumService.deleteAlbum(id);
        return ResponseEntity.noContent().build();
    }
}
