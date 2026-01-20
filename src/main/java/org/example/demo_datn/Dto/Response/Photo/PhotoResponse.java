package org.example.demo_datn.Dto.Response.Photo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhotoResponse {
    private String id;
    private String imageUrl;
    private String thumbnailUrl;
    private int width;
    private int height;
    private long size;

    private String albumId;
    private String albumTitle;

    private String ownerId;
    private String ownerUsername;

    private long likeCount;
    private long viewCount;

    private boolean liked;
}
