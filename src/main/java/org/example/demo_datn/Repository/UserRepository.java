package org.example.demo_datn.Repository;

import org.example.demo_datn.Enum.StatusUser;
import org.example.demo_datn.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsernameAndStatus(String username, StatusUser status);
    Optional<User> findByUsername(String username);
    boolean existsByUsername (String username);


}
