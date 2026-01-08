package org.example.demo_datn.Repository;

import org.example.demo_datn.Dto.Enum.AlbumStatus;
import org.example.demo_datn.Entity.Album;
import org.example.demo_datn.Entity.Permission;
import org.example.demo_datn.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, String> {

    List<Album> findByOwner(User owner);

    List<Album> findByOwner_Id(String userId);

    List<Album> findByStatus(AlbumStatus status);

    List<Album> findByLocation_Id(String locationId);
}
