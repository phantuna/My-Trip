package org.example.demo_datn.Dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class InteractionResponse {

    private boolean interacted;
    private long count;
}
