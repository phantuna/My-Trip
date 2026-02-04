package org.example.demo_datn.Controller.interaction;

import lombok.RequiredArgsConstructor;
import org.example.demo_datn.Enum.TargetType;
import org.example.demo_datn.service.interaction.InteractionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/albums")
public class AlbumInteractionController {

    private final InteractionService interactionService;

    // LIKE / UNLIKE
    @PostMapping("/{id}/like")
    public void likeAlbum(@PathVariable String id) {
        interactionService.likeOrUnlike(TargetType.ALBUM, id);
    }


    // SAVE
    @PostMapping("/{id}/save")
    public void saveAlbum(
            @PathVariable String id
    ) {
        interactionService.save( TargetType.ALBUM, id);
    }

    // UNSAVE
    @DeleteMapping("/{id}/save")
    public void unsaveAlbum(
            @PathVariable String id
    ) {
        interactionService.unsave( TargetType.ALBUM, id);
    }

    // VIEW
    @PostMapping("/{id}/view")
    public void viewAlbum(
            @PathVariable String id
    ) {
        interactionService.createView( TargetType.ALBUM, id);
    }
}
