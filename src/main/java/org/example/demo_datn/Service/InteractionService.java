package org.example.demo_datn.Service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.demo_datn.Entity.Interaction;
import org.example.demo_datn.Entity.User;
import org.example.demo_datn.Enum.InteractionType;
import org.example.demo_datn.Enum.TargetType;
import org.example.demo_datn.Repository.InteractionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InteractionService {

    private final InteractionRepository interactionRepository;

    // =========================
    // LIKE / UNLIKE
    // =========================
    public void likeOrUnlike(
            User user,
            TargetType targetType,
            String targetId
    ) {
        toggle(user, InteractionType.LIKE, targetType, targetId);
    }

    // =========================
    // SAVE
    // =========================
    public void save(
            User user,
            TargetType targetType,
            String targetId
    ) {
        boolean exists = interactionRepository
                .existsByUserAndTypeAndTargetTypeAndTargetId(
                        user, InteractionType.SAVE, targetType, targetId
                );

        if (!exists) {
            Interaction interaction = new Interaction();
            interaction.setUser(user);
            interaction.setType(InteractionType.SAVE);
            interaction.setTargetType(targetType);
            interaction.setTargetId(targetId);

            interactionRepository.save(interaction);
        }
    }

    // =========================
    // UNSAVE
    // =========================
    public void unsave(
            User user,
            TargetType targetType,
            String targetId
    ) {
        interactionRepository
                .deleteByUserAndTypeAndTargetTypeAndTargetId(
                        user, InteractionType.SAVE, targetType, targetId
                );
    }

    // =========================
    // VIEW (raw view)
    // =========================
    public void createView(
            User user,
            TargetType targetType,
            String targetId
    ) {
        Interaction interaction = new Interaction();
        interaction.setUser(user);
        interaction.setType(InteractionType.VIEW);
        interaction.setTargetType(targetType);
        interaction.setTargetId(targetId);

        interactionRepository.save(interaction);
    }

    // =========================
    // COUNT
    // =========================
    @Transactional(Transactional.TxType.SUPPORTS)
    public Map<String, Long> countByTargets(
            InteractionType type,
            TargetType targetType,
            List<String> targetIds
    ) {
        return interactionRepository
                .countByTargets(type, targetType, targetIds)
                .stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));
    }

    // =========================
    // CHECK
    // =========================
    @Transactional(Transactional.TxType.SUPPORTS)
    public boolean hasInteracted(
            User user,
            InteractionType type,
            TargetType targetType,
            String targetId
    ) {
        return interactionRepository
                .existsByUserAndTypeAndTargetTypeAndTargetId(
                        user, type, targetType, targetId
                );
    }

    // =========================
    // PRIVATE TOGGLE
    // =========================
    private void toggle(
            User user,
            InteractionType type,
            TargetType targetType,
            String targetId
    ) {
        boolean exists = interactionRepository
                .existsByUserAndTypeAndTargetTypeAndTargetId(
                        user, type, targetType, targetId
                );

        if (exists) {
            interactionRepository
                    .deleteByUserAndTypeAndTargetTypeAndTargetId(
                            user, type, targetType, targetId
                    );
        } else {
            Interaction interaction = new Interaction();
            interaction.setUser(user);
            interaction.setType(type);
            interaction.setTargetType(targetType);
            interaction.setTargetId(targetId);

            interactionRepository.save(interaction);
        }
    }
}