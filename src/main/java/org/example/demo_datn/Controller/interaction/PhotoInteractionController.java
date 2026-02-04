package org.example.demo_datn.Controller.interaction;

import lombok.RequiredArgsConstructor;
import org.example.demo_datn.Enum.TargetType;
import org.example.demo_datn.service.interaction.InteractionService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/photos")
public class PhotoInteractionController {

    private final InteractionService interactionService;

    // LIKE / UNLIKE
    @PostMapping("/{id}/like")
    public void likePhoto(
            @PathVariable String id
    ) {
        interactionService.likeOrUnlike( TargetType.PHOTO, id);
    }

    // SAVE
    @PostMapping("/{id}/save")
    public void savePhoto(
            @PathVariable String id
    ) {
        interactionService.save( TargetType.PHOTO, id);
    }

    // UNSAVE
    @DeleteMapping("/{id}/save")
    public void unsavePhoto(
            @PathVariable String id
    ) {
        interactionService.unsave( TargetType.PHOTO, id);
    }

    // VIEW (POST – vì có ghi DB)
    @PostMapping("/{id}/view")
    public void viewPhoto(
            @PathVariable String id
    ) {
        interactionService.createView(TargetType.PHOTO, id);
    }
}
