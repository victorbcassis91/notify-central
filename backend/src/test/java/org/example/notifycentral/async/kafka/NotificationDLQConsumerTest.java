package org.example.notifycentral.async.kafka;

import org.example.notifycentral.dto.MessageRequest;
import org.example.notifycentral.entity.NotificationLog;
import org.example.notifycentral.entity.UserMock;
import org.example.notifycentral.model.MessageCategory;
import org.example.notifycentral.model.NotificationChannel;
import org.example.notifycentral.redis.RedisService;
import org.example.notifycentral.repository.NotificationLogRepository;
import org.example.notifycentral.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

class NotificationDLQConsumerTest {

    private UserService userService;
    private NotificationLogRepository logRepository;
    private NotificationDLQConsumer consumer;
    private RedisService redisService;

    @BeforeEach
    void setup() {
        userService = mock(UserService.class);
        logRepository = mock(NotificationLogRepository.class);
        redisService = mock(RedisService.class);
        consumer = new NotificationDLQConsumer(userService, logRepository, redisService);
    }

    @Test
    void listenDLQ_shouldSaveLogWithErrorDetails() {
        MessageRequest failedMessage = MessageRequest.builder()
                .category(MessageCategory.FINANCE)
                .message("Failed msg")
                .build();

        UserMock user = UserMock.builder()
                .id(UUID.randomUUID())
                .name("Victor")
                .channels(List.of(NotificationChannel.EMAIL))
                .build();

        when(userService.findByCategory(MessageCategory.FINANCE)).thenReturn(List.of(user));

        consumer.listenDLQ(failedMessage, "ExceptionClass", "Something went wrong");

        verify(logRepository, times(1)).save(any(NotificationLog.class));
    }

}