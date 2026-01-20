package org.example.demo_datn.Repository;

import org.example.demo_datn.Entity.Album;
import org.example.demo_datn.Entity.User;
import org.example.demo_datn.Enum.AlbumStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, String> {

    List<Album> findByOwner(User owner);

    List<Album> findByStatusAndTitleContainingIgnoreCase(
            AlbumStatus status,
            String title
    );

    @Query("""
    SELECT a FROM Album a
    JOIN a.location l
    WHERE LOWER(l.name) LIKE LOWER(CONCAT('%', :locationName, '%'))
    """)
    List<Album> searchByLocationName(
            String locationName
    );
    @Query("""
    SELECT a FROM Album a
    JOIN a.location l
    WHERE a.status = :status
    AND l.latitude BETWEEN :latMin AND :latMax
    AND l.longitude BETWEEN :lngMin AND :lngMax
    """)
    List<Album> findNearby(
            AlbumStatus status,
            Double latMin,
            Double latMax,
            Double lngMin,
            Double lngMax
    );


}
