package org.example.demo_datn.Dto.Response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demo_datn.Dto.Enum.LocationStatus;

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
    private LocationStatus status;
}
