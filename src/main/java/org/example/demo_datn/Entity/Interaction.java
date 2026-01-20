package org.example.demo_datn.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demo_datn.Enum.InteractionType;
import org.example.demo_datn.Enum.TargetType;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "interactions",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "user_id",
                                "type",
                                "target_type",
                                "target_id"
                        }
                )
        }
)

public class Interaction extends Base{
    @ManyToOne
    private User user;

    @Enumerated(EnumType.STRING)
    private InteractionType type;
    // LIKE, VIEW, SAVE

    @Enumerated(EnumType.STRING)
    private TargetType targetType;
    // ALBUM, PHOTO

    private String targetId;
}
