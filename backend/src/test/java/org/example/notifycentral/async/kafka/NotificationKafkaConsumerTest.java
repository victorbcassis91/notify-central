package org.example.notifycentral.async.kafka;

import org.example.notifycentral.dto.MessageRequest;
import org.example.notifycentral.entity.NotificationLog;
import org.example.notifycentral.entity.UserMock;
import org.example.notifycentral.model.MessageCategory;
import org.example.notifycentral.model.NotificationChannel;
import org.example.notifycentral.redis.RedisService;
import org.example.notifycentral.repository.NotificationLogRepository;
import org.example.notifycentral.service.NotificationSender;
import org.example.notifycentral.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.*;

class NotificationKafkaConsumerTest {

    private UserService userService;
    private NotificationLogRepository logRepository;
    private NotificationSender sender;
    private NotificationKafkaConsumer consumer;
    private RedisService redisService;

    @BeforeEach
    void setup() {
        userService = mock(UserService.class);
        logRepository = mock(NotificationLogRepository.class);
        sender = mock(NotificationSender.class);
        redisService = mock(RedisService.class);

        consumer = new NotificationKafkaConsumer(
                userService,
                logRepository,
                Map.of(NotificationChannel.EMAIL.name(), sender),
                redisService
        );
    }

    @Test
    void listen_shouldSendNotificationAndSaveLog() {
        MessageRequest message = MessageRequest.builder()
                .category(MessageCategory.FINANCE)
                .message("Hello")
                .build();

        UserMock user = UserMock.builder()
                .id(UUID.randomUUID())
                .name("Victor")
                .channels(List.of(NotificationChannel.EMAIL))
                .build();

        when(userService.findByCategory(MessageCategory.FINANCE)).thenReturn(List.of(user));
        when(sender.send(user, "Hello")).thenReturn(new NotificationSender.NotificationResult(true, "ok"));

        consumer.listen(message);

        verify(sender, times(1)).send(user, "Hello");
        verify(logRepository, times(1)).save(any(NotificationLog.class));
    }

}