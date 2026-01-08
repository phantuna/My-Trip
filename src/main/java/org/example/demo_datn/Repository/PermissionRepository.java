package org.example.demo_datn.Repository;

import org.example.demo_datn.Entity.Album;
import org.example.demo_datn.Entity.Permission;
import org.example.demo_datn.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, String> {
    boolean existsByUserAndAlbum(User user, Album album);


}
