package org.example.demo_datn.Controller;

import lombok.RequiredArgsConstructor;
import org.example.demo_datn.Dto.Request.LocationRequest;
import org.example.demo_datn.Dto.Response.ApiResponse;
import org.example.demo_datn.Dto.Response.LocationResponse;
import org.example.demo_datn.service.LocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    // 1. Tạo địa điểm mới (Kèm logic tự động tính giờ vàng nếu thiếu)
    @PostMapping
    public ApiResponse<LocationResponse> create(@RequestBody LocationRequest dto) {
        ApiResponse<LocationResponse> response = new ApiResponse<>();
        response.setResult(locationService.create(dto));
        return response;
    }
    @PutMapping("/{id}")
    public ApiResponse<LocationResponse> update(@PathVariable String id, @RequestBody LocationRequest dto) {
        ApiResponse<LocationResponse> response = new ApiResponse<>();
        response.setResult(locationService.update(id, dto));
        return response;
    }
    // 2. API QUAN TRỌNG: Gợi ý địa điểm "Săn ảnh" theo giờ thực tế
    @GetMapping("/suggest")
    public ApiResponse<List<LocationResponse>> suggestSpots(
            @RequestParam Double lat,
            @RequestParam Double lng
    ) {
        ApiResponse<List<LocationResponse>> response = new ApiResponse<>();
        // Gọi service lấy List, sau đó set vào ApiResponse
        response.setResult(locationService.suggestSpotsForNow(lat, lng));
        return response;
    }

    // 3. Lấy danh sách tất cả địa điểm
    @GetMapping
    public ApiResponse<List<LocationResponse>> getAll() {
        ApiResponse<List<LocationResponse>> response = new ApiResponse<>();
        response.setResult(locationService.getAll());
        return response;
    }

    // 4. Lấy chi tiết một địa điểm
    @GetMapping("/{id}")
    public ApiResponse<LocationResponse> getById(@PathVariable String id) {
        ApiResponse<LocationResponse> response = new ApiResponse<>();
        response.setResult(locationService.getById(id));
        return response;
    }

    // 5. Duyệt hoặc Từ chối địa điểm
    @PutMapping("/{id}/approve")
    public ApiResponse<LocationResponse> approve(
            @PathVariable String id,
            @RequestParam boolean isApproved
    ) {
        ApiResponse<LocationResponse> response = new ApiResponse<>();
        response.setResult(locationService.approveLocation(id, isApproved));
        return response;
    }

    // 6. Xóa địa điểm
    @DeleteMapping("/{id}")
    public ApiResponse<String> delete(@PathVariable String id) {
        locationService.delete(id);
        ApiResponse<String> response = new ApiResponse<>();
        response.setMessage("Location deleted successfully");
        return response;
    }
}

