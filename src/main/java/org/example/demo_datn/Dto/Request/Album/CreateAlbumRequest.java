package org.example.demo_datn.Dto.Request.Album;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demo_datn.Dto.Enum.AlbumStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAlbumRequest {
    private String id;
    private String title;
    private String description;
    private String locationId;
    private AlbumStatus status; // PRIVATE / SHARED / PUBLIC
}
