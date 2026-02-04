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

    // ========== LIST ==========
    List<Interaction> findByUserAndTypeAndTargetType(
            User user,
            InteractionType type,
            TargetType targetType
    );

    @Query("""
    SELECT i.targetId, COUNT(i)
    FROM Interaction i
    WHERE i.type = :type
    AND i.targetType = :targetType
    AND i.targetId IN :targetIds
    GROUP BY i.targetId
    """)
    List<Object[]> countByTargets(
            @Param("type") InteractionType type,
            @Param("targetType") TargetType targetType,
            @Param("targetIds") List<String> targetIds
    );

    @Query("""
    SELECT i.targetId
    FROM Interaction i
    WHERE i.user = :user
    AND i.type = :type
    AND i.targetType = :targetType
    AND i.targetId IN :targetIds
    """)
    List<String> findTargetIdsUserInteracted(
            @Param("user") User user,
            @Param("type") InteractionType type,
            @Param("targetType") TargetType targetType,
            @Param("targetIds") List<String> targetIds
    );

}
