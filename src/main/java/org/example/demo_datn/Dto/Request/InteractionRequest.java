package org.example.demo_datn.Dto.Request;

import lombok.Data;
import org.example.demo_datn.Enum.InteractionType;
import org.example.demo_datn.Enum.TargetType;

@Data
public class InteractionRequest {

    private InteractionType type;      // LIKE, SAVE
    private TargetType targetType;      // ALBUM, PHOTO
    private String targetId;
}

