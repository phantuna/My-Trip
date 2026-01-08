package org.example.demo_datn.Dto.Request.Album;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demo_datn.Dto.Enum.AlbumStatus;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateAlbumRequest {
    private String title;
    private String description;
    private AlbumStatus status;
}
