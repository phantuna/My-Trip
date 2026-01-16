package org.example.demo_datn.Dto.Request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationRequest {
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String description;
}
