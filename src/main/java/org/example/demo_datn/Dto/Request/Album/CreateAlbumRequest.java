package org.example.demo_datn.Dto.Request.Album;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demo_datn.Enum.AlbumStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAlbumRequest {
    private String id;
    private String title;
    private String description;
    private String locationId;

    @Enumerated(EnumType.STRING)
    private AlbumStatus status; // PRIVATE / SHARED / PUBLIC
}
