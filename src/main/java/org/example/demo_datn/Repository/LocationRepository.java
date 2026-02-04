package org.example.demo_datn.Repository;

import org.example.demo_datn.Enum.LocationStatus;
import org.example.demo_datn.Entity.Locations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Locations, String> {
    List<Locations> findByStatus(LocationStatus status);

    @Query(value = """
        SELECT * FROM locations l 
        WHERE l.status = 'APPROVAL'
        AND (6371 * acos(cos(radians(:lat)) * cos(radians(l.latitude)) * cos(radians(l.longitude) - radians(:lng)) + sin(radians(:lat)) * sin(radians(l.latitude)))) < :radius
        AND :currentMinutes BETWEEN l.best_time_start AND l.best_time_end
    """, nativeQuery = true)
    List<Locations> findBestSpotsNow(@Param("lat") double lat,
                                     @Param("lng") double lng,
                                     @Param("radius") double radius,
                                     @Param("currentMinutes") int currentMinutes);

    // [NEW] Tìm địa điểm gần nhất (Fallback nếu không có giờ vàng)
    @Query(value = """
        SELECT * FROM locations l 
        WHERE l.status = 'APPROVAL'
        AND (6371 * acos(cos(radians(:lat)) * cos(radians(l.latitude)) * cos(radians(l.longitude) - radians(:lng)) + sin(radians(:lat)) * sin(radians(l.latitude)))) < :radius
    """, nativeQuery = true)
    List<Locations> findNearby(@Param("lat") double lat,
                               @Param("lng") double lng,
                               @Param("radius") double radius);
}
