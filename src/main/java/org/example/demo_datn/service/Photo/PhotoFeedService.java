package org.example.demo_datn.service.Photo;

import lombok.RequiredArgsConstructor;
import org.example.demo_datn.Dto.Response.Photo.PhotoResponse;
import org.example.demo_datn.Entity.Photo;
import org.example.demo_datn.Entity.User;
import org.example.demo_datn.Enum.InteractionType;
import org.example.demo_datn.Enum.TargetType;
import org.example.demo_datn.service.interaction.InteractionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PhotoFeedService {

    private final InteractionService interactionService;

    public List<PhotoResponse> buildFeed(
            List<Photo> photos,
            User user
    ) {
        List<String> photoIds = photos.stream()
                .map(Photo::getId)
                .toList();

        Map<String, Long> likeCounts = interactionService.countByTargets(
                InteractionType.LIKE,
                TargetType.PHOTO,
                photoIds
        );

        Map<String, Long> viewCounts = interactionService.countByTargets(
                InteractionType.VIEW,
                TargetType.PHOTO,
                photoIds
        );

        Set<String> likedIds = interactionService.getUserInteractedTargetIds(
                InteractionType.LIKE,
                TargetType.PHOTO,
                photoIds
        );

        Set<String> savedIds = interactionService.getUserInteractedTargetIds(
                InteractionType.SAVE,
                TargetType.PHOTO,
                photoIds
        );

        return photos.stream()
                .map(p -> PhotoResponse.builder()
                        .id(p.getId())
                        .imageUrl(p.getImageUrl())
                        .thumbnailUrl(p.getThumbnailUrl())
                        .width(p.getWidth())
                        .height(p.getHeight())
                        .size(p.getSize())
                        .albumId(p.getAlbum().getId())
                        .albumTitle(p.getAlbum().getTitle())
                        .ownerId(p.getOwner().getId())
                        .ownerUsername(p.getOwner().getUsername())
                        .likeCount(likeCounts.getOrDefault(p.getId(), 0L))
                        .viewCount(viewCounts.getOrDefault(p.getId(), 0L))
                        .liked(likedIds.contains(p.getId()))
                        .saved(savedIds.contains(p.getId())) //
                        .build()
                )
                .toList();

    }
}
