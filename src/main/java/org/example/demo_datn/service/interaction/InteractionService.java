package org.example.demo_datn.service.interaction;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.demo_datn.Entity.Interaction;
import org.example.demo_datn.Entity.User;
import org.example.demo_datn.Enum.InteractionType;
import org.example.demo_datn.Enum.TargetType;
import org.example.demo_datn.Exception.AppException;
import org.example.demo_datn.Exception.ErrorCode;
import org.example.demo_datn.Repository.InteractionRepository;
import org.example.demo_datn.Repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InteractionService {

    private final InteractionRepository interactionRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }
    // =========================
    // LIKE / UNLIKE
    // =========================
    public void likeOrUnlike(
            TargetType targetType,
            String targetId
    ) {
        toggle( InteractionType.LIKE, targetType, targetId);
    }

    // =========================
    // SAVE
    // =========================
    public void save(
            TargetType targetType,
            String targetId
    ) {
        User user = getCurrentUser();

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
            TargetType targetType,
            String targetId
    ) {
        User user = getCurrentUser();
        interactionRepository
                .deleteByUserAndTypeAndTargetTypeAndTargetId(
                        user, InteractionType.SAVE, targetType, targetId
                );
    }

    // =========================
    // VIEW (raw view)
    // =========================
    public void createView(
            TargetType targetType,
            String targetId
    ) {
        User user = getCurrentUser();

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
            InteractionType type,
            TargetType targetType,
            String targetId
    ) {
        User user = getCurrentUser();

        return interactionRepository
                .existsByUserAndTypeAndTargetTypeAndTargetId(
                        user, type, targetType, targetId
                );
    }

    // =========================
    // PRIVATE TOGGLE
    // =========================
    private void toggle(
            InteractionType type,
            TargetType targetType,
            String targetId
    ) {
        User user = getCurrentUser();

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

    @Transactional(Transactional.TxType.SUPPORTS)
    public Set<String> getUserInteractedTargetIds(
            InteractionType type,
            TargetType targetType,
            List<String> targetIds
    ) {
        User user = getCurrentUser();

        return new HashSet<>(
                interactionRepository.findTargetIdsUserInteracted(
                        user, type, targetType, targetIds
                )
        );
    }


}