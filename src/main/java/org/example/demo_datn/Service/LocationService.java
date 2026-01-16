package org.example.demo_datn.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.demo_datn.Dto.Enum.LocationStatus;
import org.example.demo_datn.Dto.Request.LocationRequest;
import org.example.demo_datn.Dto.Response.LocationResponse;
import org.example.demo_datn.Entity.Locations;
import org.example.demo_datn.Entity.User;
import org.example.demo_datn.Exception.AppException;
import org.example.demo_datn.Exception.ErrorCode;
import org.example.demo_datn.Repository.LocationRepository;
import org.example.demo_datn.Repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LocationService {

    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    public LocationResponse create(LocationRequest dto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Locations location = new Locations();
        location.setName(dto.getName());
        location.setAddress(dto.getAddress());
        location.setLatitude(dto.getLatitude());
        location.setLongitude(dto.getLongitude());
        location.setDescription(dto.getDescription());

        // Mặc định
        location.setStatus(LocationStatus.PENDING);

        // === AUTO APPROVE CHECK ===
        if (isAutoApproved(dto)) {
            location.setStatus(LocationStatus.AUTO_APPROVED);
        }

        locationRepository.save(location);

        return mapToResponse(location);
    }


    public LocationResponse getById(String id) {
        Locations location = locationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.LOCATION_NOT_FOUND));

        return mapToResponse(location);
    }

    public List<LocationResponse> getAll() {
        return locationRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void delete(String id) {
        Locations location = locationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.LOCATION_NOT_FOUND));
        locationRepository.delete(location);
    }

    public LocationResponse approveLocation(String locationId, boolean approve) {

        Locations location = locationRepository.findById(locationId)
                .orElseThrow(() -> new AppException(ErrorCode.LOCATION_NOT_FOUND));

        if (location.getStatus() != LocationStatus.PENDING) {
            throw new AppException(ErrorCode.INVALID_STATUS);
        }

        location.setStatus(
                approve ? LocationStatus.APPROVAL : LocationStatus.REJECTED
        );

        locationRepository.save(location);

        return mapToResponse(location);
    }

    private LocationResponse mapToResponse(Locations location) {
        return LocationResponse.builder()
                .id(location.getId())
                .name(location.getName())
                .address(location.getAddress())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .description(location.getDescription())
                .build();
    }

    private boolean isAutoApproved(LocationRequest dto) {

        // Rule 1: mô tả đủ dài
        if (dto.getDescription() == null || dto.getDescription().length() < 20) {
            return false;
        }

        // Rule 2: gần địa điểm đã được duyệt
        double MAX_DISTANCE_KM = 2.0;

        List<Locations> approvedLocations =
                locationRepository.findByStatus(LocationStatus.APPROVAL);

        for (Locations loc : approvedLocations) {
            double distance = distanceKm(
                    dto.getLatitude(),
                    dto.getLongitude(),
                    loc.getLatitude(),
                    loc.getLongitude()
            );

            if (distance < MAX_DISTANCE_KM) {
                return true;
            }
        }

        return false;
    }
    private double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }


}

