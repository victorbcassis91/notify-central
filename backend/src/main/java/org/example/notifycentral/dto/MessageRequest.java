package org.example.notifycentral.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.notifycentral.model.MessageCategory;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageRequest {

    @NotNull(message = "category is required")
    private MessageCategory category;

    @NotBlank(message = "message must not be empty")
    private String message;

    private String statusOrder;
}
