package org.example.demo_datn.Dto.Request.Album;


import lombok.Data;
import org.example.demo_datn.Dto.Enum.AlbumStatus;

@Data
public class CreateAlbumRequest {
    private String id;
    private String title;
    private String description;
    private String locationId;
    private AlbumStatus status; // PRIVATE / SHARED / PUBLIC
}
