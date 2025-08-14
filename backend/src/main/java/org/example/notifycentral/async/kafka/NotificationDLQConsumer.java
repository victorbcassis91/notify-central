package org.example.notifycentral.async.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notifycentral.dto.MessageRequest;
import org.example.notifycentral.entity.NotificationLog;
import org.example.notifycentral.entity.UserMock;
import org.example.notifycentral.model.MessageCategory;
import org.example.notifycentral.model.NotificationChannel;
import org.example.notifycentral.model.StatusOrder;
import org.example.notifycentral.redis.RedisService;
import org.example.notifycentral.repository.NotificationLogRepository;
import org.example.notifycentral.service.UserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "notifycentral.notification-type", havingValue = "kafka")
public class NotificationDLQConsumer {

    private final UserService userService;
    private final NotificationLogRepository notificationLogRepository;
    private final RedisService redisService;

    @KafkaListener(topics = "notification-messages.DLQ", groupId = "dlq-group")
    public void listenDLQ(MessageRequest failedMessage,
                          @Header(name = "exception", required = false) String exceptionClass,
                          @Header(name = "exception-message", required = false) String exceptionMsg) {
        log.info("Received notification message from kafka: {}", failedMessage);

        List<UserMock> subscribers = userService.findByCategory(failedMessage.getCategory());

        for (UserMock subscriber : subscribers) {
            for (NotificationChannel channel : subscriber.getChannels()) {

                MessageCategory category = failedMessage.getCategory();

                NotificationLog log = NotificationLog.builder()
                        .category(category)
                        .channel(channel)
                        .userId(subscriber.getId().toString())
                        .userName(subscriber.getName())
                        .message(failedMessage.getMessage())
                        .delivered(false)
                        .details("Error sending notification: " + exceptionMsg)
                        .timestamp(OffsetDateTime.now())
                        .build();

                notificationLogRepository.save(log);

                redisService.setValue(failedMessage.getStatusOrder(), StatusOrder.ERROR.name(), 6000);
            }
        }
    }

}
