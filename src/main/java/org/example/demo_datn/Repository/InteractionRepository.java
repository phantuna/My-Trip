package org.example.demo_datn.Repository;

import org.example.demo_datn.Entity.Interaction;
import org.example.demo_datn.Entity.User;
import org.example.demo_datn.Enum.InteractionType;
import org.example.demo_datn.Enum.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InteractionRepository extends JpaRepository <Interaction,String> {
    // ========== CHECK ==========
    boolean existsByUserAndTypeAndTargetTypeAndTargetId(
            User user,
            InteractionType type,
            TargetType targetType,
            String targetId
    );

    // ========== DELETE (unlike / unsave) ==========
    void deleteByUserAndTypeAndTargetTypeAndTargetId(
            User user,
            InteractionType type,
            TargetType targetType,
            String targetId
    );

    // ========== COUNT ==========
    long countByTypeAndTargetTypeAndTargetId(
            InteractionType type,
            TargetType targetType,
            String targetId
    );

    // ========== LIST ==========
    List<Interaction> findByUserAndTypeAndTargetType(
            User user,
            InteractionType type,
            TargetType targetType
    );


}
