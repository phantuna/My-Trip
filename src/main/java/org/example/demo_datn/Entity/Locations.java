package org.example.demo_datn.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demo_datn.Enum.LocationStatus;

@Entity
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Locations extends Base{
    private String name;
    private String description;
    private Double latitude;;
    private Double longitude;
    private String address;

    @Enumerated(EnumType.STRING)
    private LocationStatus status;

}
