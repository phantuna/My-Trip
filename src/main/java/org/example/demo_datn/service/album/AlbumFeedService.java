package org.example.demo_datn.service.album;

import lombok.RequiredArgsConstructor;
import org.example.demo_datn.Dto.Response.Album.AlbumResponse;
import org.example.demo_datn.Entity.Album;
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
public class AlbumFeedService {

    private final InteractionService interactionService;

    public List<AlbumResponse> buildFeed(List<Album> albums) {

        List<String> albumIds = albums.stream()
                .map(Album::getId)
                .toList();

        Map<String, Long> likeCounts = interactionService.countByTargets(
                InteractionType.LIKE,
                TargetType.ALBUM,
                albumIds
        );

        Map<String, Long> viewCounts = interactionService.countByTargets(
                InteractionType.VIEW,
                TargetType.ALBUM,
                albumIds
        );

        Set<String> likedIds = interactionService.getUserInteractedTargetIds(
                InteractionType.LIKE,
                TargetType.ALBUM,
                albumIds
        );

        Set<String> savedIds = interactionService.getUserInteractedTargetIds(
                InteractionType.SAVE,
                TargetType.ALBUM,
                albumIds
        );

        return albums.stream()
                .map(a -> AlbumResponse.builder()
                        .id(a.getId())
                        .title(a.getTitle())
                        .description(a.getDescription())
                        .status(a.getStatus())
                        .locationId(a.getLocation() != null ? a.getLocation().getId() : null)
                        .locationName(a.getLocation() != null ? a.getLocation().getName() : null)
                        .ownerId(a.getOwner().getId())
                        .ownerUsername(a.getOwner().getUsername())
                        .likeCount(likeCounts.getOrDefault(a.getId(), 0L))
                        .viewCount(viewCounts.getOrDefault(a.getId(), 0L))
                        .liked(likedIds.contains(a.getId()))
                        .saved(savedIds.contains(a.getId()))
                        .build()
                )
                .toList();
    }
}
