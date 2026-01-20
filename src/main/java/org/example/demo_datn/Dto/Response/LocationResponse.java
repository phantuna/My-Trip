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
}
