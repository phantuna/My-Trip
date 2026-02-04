package org.example.demo_datn.Dto.Response;


import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demo_datn.Enum.LocationStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationResponse {
    private String id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String description;

    @Enumerated(EnumType.STRING)
    private LocationStatus status;

    private String bestTimeDisplay; // Trả về dạng "05:30 - 06:30"
    private String recommendedLens;
    private String viewDirection;
    private String thumbnailUrl;
    private boolean isGoldenHourNow;// Cờ báo hiệu đang là giờ vàng
}
