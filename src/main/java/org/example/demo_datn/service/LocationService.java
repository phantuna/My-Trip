package org.example.demo_datn.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.demo_datn.Enum.LocationStatus;
import org.example.demo_datn.Dto.Request.LocationRequest;
import org.example.demo_datn.Dto.Response.LocationResponse;
import org.example.demo_datn.Entity.Locations;
import org.example.demo_datn.Entity.User;
import org.example.demo_datn.Exception.AppException;
import org.example.demo_datn.Exception.ErrorCode;
import org.example.demo_datn.Repository.LocationRepository;
import org.example.demo_datn.Repository.UserRepository;
import org.example.demo_datn.Util.SunCalcUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional
public class LocationService {

    private final LocationRepository locationRepository;
    private final UserRepository userRepository;

    // === [HELPER] Chuyển đổi giờ dạng Double (6.30) sang phút (390) ===
    private Integer convertDecimalToMinutes(Double time) {
        if (time == null) return null;
        int hours = time.intValue(); // Lấy phần nguyên (Giờ)
        // Lấy phần thập phân nhân 100 để ra phút (VD: 0.3 -> 30)
        // Dùng Math.round để tránh sai số số học (floating point error)
        int minutes = (int) Math.round((time - hours) * 100);
        return hours * 60 + minutes;
    }

    // === Helper format phút sang chuỗi HH:mm để hiển thị ===
    private String formatMinutes(int minutes) {
        return LocalTime.ofSecondOfDay(minutes * 60L).toString();
    }

    private LocationResponse mapToResponse(Locations location) {
        int currentMinutes = LocalTime.now().get(ChronoField.MINUTE_OF_DAY);

        // Kiểm tra xem giờ hiện tại có nằm trong giờ vàng của địa điểm không
        boolean isGolden = false;
        if (location.getBestTimeStart() != null && location.getBestTimeEnd() != null) {
            isGolden = currentMinutes >= location.getBestTimeStart() && currentMinutes <= location.getBestTimeEnd();
        }

        String timeDisplay = "N/A";
        if (location.getBestTimeStart() != null) {
            timeDisplay = formatMinutes(location.getBestTimeStart()) + " - " + formatMinutes(location.getBestTimeEnd());
        }

        return LocationResponse.builder()
                .id(location.getId())
                .name(location.getName())
                .address(location.getAddress())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .description(location.getDescription())
                .status(location.getStatus())
                // New fields
                .bestTimeDisplay(timeDisplay)
                .recommendedLens(location.getRecommendedLens())
                .viewDirection(location.getViewDirection())
                .thumbnailUrl(location.getThumbnailUrl())
                .isGoldenHourNow(isGolden)
                .build();
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    // === [NEW] API GỢI Ý ĐỊA ĐIỂM (PHOTO SCOUT) ===
    public List<LocationResponse> suggestSpotsForNow(Double lat, Double lng) {
        int currentMinutes = LocalTime.now().get(ChronoField.MINUTE_OF_DAY);

        // 1. Ưu tiên tìm các điểm đang là giờ vàng trong bán kính 20km
        List<Locations> spots = locationRepository.findBestSpotsNow(lat, lng, 500.0, currentMinutes);

        // 2. Nếu không có (ví dụ buổi trưa), trả về các điểm gần nhất
        if (spots.isEmpty()) {
            spots = locationRepository.findNearby(lat, lng, 500.0);
        }

        return spots.stream().map(this::mapToResponse).toList();
    }

    // === [UPDATE] CREATE ===
    public LocationResponse create(LocationRequest dto) {
        Locations duplicate = findDuplicateLocation(dto.getLatitude(), dto.getLongitude());
        if (duplicate != null) return mapToResponse(duplicate);

        Locations location = new Locations();
        location.setName(dto.getName());
        location.setAddress(dto.getAddress());
        location.setLatitude(dto.getLatitude());
        location.setLongitude(dto.getLongitude());
        location.setDescription(dto.getDescription());
        location.setRecommendedLens(dto.getRecommendedLens());
        location.setViewDirection(dto.getViewDirection());

        // [LOGIC GIỜ]
        // Case 1: User nhập tay (dạng Double 16.30)
        if (dto.getBestTimeStart() != null && dto.getBestTimeEnd() != null) {
            location.setBestTimeStart(convertDecimalToMinutes(dto.getBestTimeStart()));
            location.setBestTimeEnd(convertDecimalToMinutes(dto.getBestTimeEnd()));
        }
        // Case 2: Tự động tính toán (SunCalc)
        else {
            int[] sunTimes = SunCalcUtil.calculateSunriseSunset(
                    dto.getLatitude(), dto.getLongitude(), LocalDate.now()
            );
            // Mặc định set giờ vàng là 1 tiếng trước khi lặn (Golden Hour chiều)
            location.setBestTimeStart(sunTimes[1] - 60);
            location.setBestTimeEnd(sunTimes[1]);
        }

        location.setStatus(LocationStatus.PENDING);
        if (isAutoApproved(dto)) location.setStatus(LocationStatus.APPROVAL);

        locationRepository.save(location);
        return mapToResponse(location);
    }

    // === [UPDATE] UPDATE ===
    public LocationResponse update(String id, LocationRequest dto) {
        Locations location = locationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.LOCATION_NOT_FOUND));

        location.setName(dto.getName());
        location.setAddress(dto.getAddress());
        location.setLatitude(dto.getLatitude());
        location.setLongitude(dto.getLongitude());
        location.setDescription(dto.getDescription());
        location.setRecommendedLens(dto.getRecommendedLens());
        location.setViewDirection(dto.getViewDirection());

        // Cập nhật giờ nếu user có gửi lên
        if (dto.getBestTimeStart() != null && dto.getBestTimeEnd() != null) {
            location.setBestTimeStart(convertDecimalToMinutes(dto.getBestTimeStart()));
            location.setBestTimeEnd(convertDecimalToMinutes(dto.getBestTimeEnd()));
        }

        Locations saved = locationRepository.save(location);
        return mapToResponse(saved);
    }

    // ... (Giữ nguyên các hàm isAutoApproved, distanceKm, findDuplicateLocation, getById, getAll, delete, approveLocation) ...

    private boolean isAutoApproved(LocationRequest dto) {
        if (dto.getDescription() == null || dto.getDescription().length() < 20) return false;

        double MAX_DISTANCE_KM = 1.0;
        List<Locations> approvedLocations = locationRepository.findByStatus(LocationStatus.APPROVAL);
        for (Locations loc : approvedLocations) {
            double distance = distanceKm(dto.getLatitude(), dto.getLongitude(), loc.getLatitude(), loc.getLongitude());
            if (distance < MAX_DISTANCE_KM) return true;
        }
        return false;
    }

    private double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private Locations findDuplicateLocation(double lat, double lon) {
        double DUP_RADIUS_KM = 0.1;
        List<Locations> approved = locationRepository.findByStatus(LocationStatus.APPROVAL);
        for (Locations loc : approved) {
            double d = distanceKm(lat, lon, loc.getLatitude(), loc.getLongitude());
            if (d <= DUP_RADIUS_KM) return loc;
        }
        return null;
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
        location.setStatus(approve ? LocationStatus.APPROVAL : LocationStatus.REJECTED);
        locationRepository.save(location);
        return mapToResponse(location);
    }
}