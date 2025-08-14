package org.example.notifycentral.async.executor;

import org.example.notifycentral.entity.NotificationLog;
import org.example.notifycentral.entity.UserMock;
import org.example.notifycentral.model.MessageCategory;
import org.example.notifycentral.model.NotificationChannel;
import org.example.notifycentral.redis.RedisService;
import org.example.notifycentral.repository.NotificationLogRepository;
import org.example.notifycentral.service.NotificationSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AsyncNotificationServiceTest {

    private NotificationLogRepository logRepository;
    private NotificationSender emailSender;
    private AsyncNotificationService service;
    private RedisService redisService;

    @BeforeEach
    void setUp() {
        logRepository = mock(NotificationLogRepository.class);
        emailSender = mock(NotificationSender.class);
        redisService = mock(RedisService.class);

        service = new AsyncNotificationService(logRepository, Map.of("EMAIL", emailSender), redisService);
        service.maxAttempts = 2;
        service.initialBackoffMs = 1;
    }

    @Test
    void sendWithRetry_shouldSaveLogWhenSuccessOnFirstTry() {
        UserMock user = UserMock.builder()
                .id(UUID.randomUUID())
                .name("Victor")
                .email("victor@example.com")
                .build();

        when(emailSender.send(any(), any()))
                .thenReturn(new NotificationSender.NotificationResult(true, "ok"));

        String statusOrder = "statusOrder:" + UUID.randomUUID();
        service.sendWithRetry(user, MessageCategory.SPORTS, NotificationChannel.EMAIL, "msg", statusOrder);

        verify(emailSender, times(1)).send(user, "msg");
        verify(logRepository).save(any(NotificationLog.class));
    }

    @Test
    void sendWithRetry_shouldRetryWhenFirstAttemptFails() {
        UserMock user = UserMock.builder()
                .id(UUID.randomUUID())
                .name("Victor")
                .email("victor@example.com")
                .build();

        when(emailSender.send(any(), any()))
                .thenReturn(new NotificationSender.NotificationResult(false, "fail"))
                .thenReturn(new NotificationSender.NotificationResult(true, "ok"));

        String statusOrder = "statusOrder:" + UUID.randomUUID();
        service.sendWithRetry(user, MessageCategory.SPORTS, NotificationChannel.EMAIL, "msg", statusOrder);

        verify(emailSender, times(2)).send(user, "msg");
        verify(logRepository).save(any(NotificationLog.class));
    }

    @Test
    void sendWithRetry_shouldLogErrorWhenSenderNotFoundBecauseItNotHasSmsChannel() {
        UserMock user = UserMock.builder()
                .id(UUID.randomUUID())
                .name("Victor")
                .email("victor@example.com")
                .build();

        String statusOrder = "statusOrder:" + UUID.randomUUID();
        service.sendWithRetry(user, MessageCategory.SPORTS, NotificationChannel.SMS, "msg", statusOrder);

        verify(emailSender, never()).send(any(), any());
        verify(logRepository).save(any(NotificationLog.class));
    }
}