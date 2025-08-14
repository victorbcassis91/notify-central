package org.example.notifycentral.async.kafka;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.example.notifycentral.dto.MessageRequest;
import org.example.notifycentral.model.MessageCategory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationKafkaProducer {

    private final KafkaTemplate<String, MessageRequest> kafkaTemplate;

    public void send(@NotNull(message = "category is required") MessageCategory messageCategory, String message, String statusOrder) {
        kafkaTemplate.send("notification-messages", MessageRequest.builder()
                .category(messageCategory)
                .message(message)
                .statusOrder(statusOrder)
                .build());
    }

}
