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

    private Integer bestTimeStart;
    private Integer bestTimeEnd;

    // Gợi ý thiết bị: "Lens góc rộng", "Flycam", "Chân máy"
    private String recommendedLens;

    // Hướng nhìn: "Đông", "Tây" (quan trọng để đón bình minh/hoàng hôn)
    private String viewDirection;

    // Ảnh đại diện đẹp nhất (lấy từ album)
    private String thumbnailUrl;

}
