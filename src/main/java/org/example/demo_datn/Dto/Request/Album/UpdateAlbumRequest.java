package org.example.demo_datn.Dto.Request.Album;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demo_datn.Enum.AlbumStatus;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateAlbumRequest {
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private AlbumStatus status;
}
