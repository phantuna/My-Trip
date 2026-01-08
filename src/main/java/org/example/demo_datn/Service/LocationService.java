package org.example.demo_datn.Service;

import lombok.RequiredArgsConstructor;
import org.example.demo_datn.Dto.Enum.LocationStatus;
import org.example.demo_datn.Entity.Locations;
import org.example.demo_datn.Entity.User;
import org.example.demo_datn.Repository.LocationRepository;
import org.example.demo_datn.Repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    public Locations createDemoLocation() {
        User user = userRepository.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName()
        ).orElseThrow();

        Locations loc = new Locations();
        loc.setName("Hồ Xuân Hương");
        loc.setAddress("Đà Lạt, Lâm Đồng");
        loc.setLatitude(11.9404);
        loc.setLongitude(108.4583);
        loc.setStatus(LocationStatus.APPROVAL);
        loc.setCreatedBy(user.getCreatedBy());

        return locationRepository.save(loc);
    }
}
