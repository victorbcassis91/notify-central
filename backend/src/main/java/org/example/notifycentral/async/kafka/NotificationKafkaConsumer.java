package org.example.notifycentral.async.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notifycentral.dto.MessageRequest;
import org.example.notifycentral.entity.NotificationLog;
import org.example.notifycentral.entity.UserMock;
import org.example.notifycentral.model.NotificationChannel;
import org.example.notifycentral.model.StatusOrder;
import org.example.notifycentral.redis.RedisService;
import org.example.notifycentral.repository.NotificationLogRepository;
import org.example.notifycentral.service.NotificationSender;
import org.example.notifycentral.service.UserService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "notifycentral.notification-type", havingValue = "kafka")
public class NotificationKafkaConsumer {

    private final UserService userService;
    private final NotificationLogRepository notificationLogRepository;
    private final Map<String, NotificationSender> senders;
    private final RedisService redisService;

    @KafkaListener(topics = "notification-messages", groupId = "notify-group")
    public void listen(MessageRequest messageRequest) {
        log.info("Received notification message from kafka: {}", messageRequest);

        List<UserMock> subscribers = userService.findByCategory(messageRequest.getCategory());

        for (UserMock subscriber : subscribers) {
            for (NotificationChannel channel : subscriber.getChannels()) {
                NotificationSender sender = senders.get(channel.name());

                NotificationSender.NotificationResult result = sender.send(subscriber, messageRequest.getMessage());
                boolean delivered = result.success;
                String details = result.details;

                NotificationLog log = NotificationLog.builder()
                        .category(messageRequest.getCategory())
                        .channel(channel)
                        .userId(subscriber.getId().toString())
                        .userName(subscriber.getName())
                        .message(messageRequest.getMessage())
                        .delivered(delivered)
                        .details(details)
                        .timestamp(OffsetDateTime.now())
                        .build();

                notificationLogRepository.save(log);

                redisService.setValue(messageRequest.getStatusOrder(), StatusOrder.SUCCESS.name(), 6000);
            }
        }
    }

}
