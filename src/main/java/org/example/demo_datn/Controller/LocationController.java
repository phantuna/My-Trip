package org.example.demo_datn.Controller;

import lombok.RequiredArgsConstructor;
import org.example.demo_datn.Entity.Locations;
import org.example.demo_datn.Service.LocationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping()
    public Locations createDemo() {
        return locationService.createDemoLocation();
    }
}

