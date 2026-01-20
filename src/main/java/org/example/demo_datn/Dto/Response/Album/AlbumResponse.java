package org.example.demo_datn.Dto.Response.Album;

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
public class AlbumResponse {

    private String id;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private AlbumStatus status;

    // Location (rút gọn)
    private String locationId;
    private String locationName;

    // Owner (rút gọn)
    private String ownerId;
    private String ownerUsername;

}