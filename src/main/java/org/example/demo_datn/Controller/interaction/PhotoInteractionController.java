package org.example.demo_datn.Controller.interaction;
import lombok.AllArgsConstructor;
import org.example.demo_datn.Entity.User;
import org.example.demo_datn.Enum.TargetType;
import org.example.demo_datn.Service.InteractionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class PhotoInteractionController {
    private final InteractionService interactionService;

    @PostMapping("/photos/{id}/like")
    public void likePhoto(
            @PathVariable String id,
            @AuthenticationPrincipal User user
    ) {
        interactionService.likeOrUnlike(user, TargetType.PHOTO, id);
    }

    @PostMapping("/photos/{id}/save")
    public void savePhoto(
            @PathVariable String id,
            @AuthenticationPrincipal User user
    ) {
        interactionService.save(user, TargetType.PHOTO, id);
    }

    @DeleteMapping("/photos/{id}/save")
    public void unsavePhoto(
            @PathVariable String id,
            @AuthenticationPrincipal User user
    ) {
        interactionService.unsave(user, TargetType.PHOTO, id);
    }

    @GetMapping("/photos/{id}/view")
    public void viewPhoto(
            @PathVariable String id,
            @AuthenticationPrincipal User user
    ) {
        interactionService.createView(user, TargetType.PHOTO, id);
    }
}
