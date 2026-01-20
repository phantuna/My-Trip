package org.example.demo_datn.Controller.interaction;


import lombok.AllArgsConstructor;
import org.example.demo_datn.Entity.User;
import org.example.demo_datn.Enum.TargetType;
import org.example.demo_datn.Service.InteractionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class AlbumInteractionController {
    private final InteractionService interactionService;

    @PostMapping("/albums/{id}/like")
    public void likeAlbum(
            @PathVariable String id,
            @AuthenticationPrincipal User user
    ) {
        interactionService.likeOrUnlike(user, TargetType.ALBUM, id);
    }

    @PostMapping("/albums/{id}/save")
    public void saveAlbum(
            @PathVariable String id,
            @AuthenticationPrincipal User user
    ) {
        interactionService.save(user, TargetType.ALBUM, id);
    }

    @DeleteMapping("/albums/{id}/save")
    public void unsaveAlbum(
            @PathVariable String id,
            @AuthenticationPrincipal User user
    ) {
        interactionService.unsave(user, TargetType.ALBUM, id);
    }

    @GetMapping("/albums/{id}/view")
    public void viewAlbum(
            @PathVariable String id,
            @AuthenticationPrincipal User user
    ) {
        interactionService.createView(user, TargetType.ALBUM, id);
    }

}
