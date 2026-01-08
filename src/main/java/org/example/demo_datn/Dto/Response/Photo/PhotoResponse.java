package org.example.demo_datn.Dto.Response.Photo;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PhotoResponse {
    private Long id;
    private String imageUrl;
    private String thumbnailUrl;
    private int width;
    private int height;
    private long size;
}
