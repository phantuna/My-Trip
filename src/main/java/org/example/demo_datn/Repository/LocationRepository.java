package org.example.demo_datn.Repository;

import org.example.demo_datn.Dto.Enum.LocationStatus;
import org.example.demo_datn.Entity.Locations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Locations, String> {

    List<Locations> findByStatus(LocationStatus status);
}
