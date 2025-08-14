package org.example.notifycentral.controller;

import org.example.notifycentral.async.executor.NotificationDispatcher;
import org.example.notifycentral.async.kafka.NotificationKafkaProducer;
import org.example.notifycentral.dto.MessageRequest;
import org.example.notifycentral.dto.PageResponse;
import org.example.notifycentral.entity.NotificationLog;
import org.example.notifycentral.model.MessageCategory;
import org.example.notifycentral.model.NotificationType;
import org.example.notifycentral.model.StatusOrder;
import org.example.notifycentral.redis.RedisService;
import org.example.notifycentral.repository.NotificationLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class NotificationControllerTest {

    @Mock
    private NotificationDispatcher notificationDispatcher;

    @Mock
    private NotificationKafkaProducer notificationKafkaProducer;

    @Mock
    private NotificationLogRepository notificationLogRepository;

    @Mock
    private RedisService redisService;

    @InjectMocks
    private NotificationController controller;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void submitMessage_executorType_success() throws Exception {
        // Arrange
        ReflectionTestUtils.setField(controller, "notificationTyoe", NotificationType.EXECUTOR);
        MessageRequest request = new MessageRequest(MessageCategory.FINANCE, "Hello", StatusOrder.PENDING.name());

        // Act
        String statusOrder = controller.submitMessage(request);

        // Assert
        assertTrue(statusOrder.startsWith("statusOrder:"));
        verify(notificationDispatcher).dispatch(eq(MessageCategory.FINANCE), eq("Hello"), anyString());
        verifyNoInteractions(notificationKafkaProducer);
    }

    @Test
    void submitMessage_kafkaType_success() throws Exception {
        // Arrange
        ReflectionTestUtils.setField(controller, "notificationTyoe", NotificationType.KAFKA);
        MessageRequest request = new MessageRequest(MessageCategory.FINANCE, "Hi", StatusOrder.PENDING.name());

        // Act
        String statusOrder = controller.submitMessage(request);

        // Assert
        assertTrue(statusOrder.startsWith("statusOrder:"));
        verify(notificationKafkaProducer).send(eq(MessageCategory.FINANCE), eq("Hi"), anyString());
        verifyNoInteractions(notificationDispatcher);
    }

    @Test
    void getNotifications_paginationWorks() {
        // Arrange
        NotificationLog log1 = new NotificationLog();
        log1.setTimestamp(OffsetDateTime.now(ZoneOffset.UTC));
        NotificationLog log2 = new NotificationLog();
        log2.setTimestamp(OffsetDateTime.now().minusSeconds(60));

        when(notificationLogRepository.findAll()).thenReturn(List.of(log1, log2));

        // Act
        PageResponse<NotificationLog> page = controller.getNotifications(0, 1);

        // Assert
        assertEquals(2, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
        assertEquals(1, page.getContent().size());
    }

    @Test
    void getStatusOrder_returnsValueFromRedis() {
        // Arrange
        String statusKey = "statusOrder:" + UUID.randomUUID();
        when(redisService.getValue(statusKey)).thenReturn(StatusOrder.SUCCESS.name());

        // Act
        StatusOrder result = controller.getStatusOrder(statusKey);

        // Assert
        assertEquals(StatusOrder.SUCCESS, result);
    }

    @Test
    void getCategories_returnsAllEnums() {
        // Act
        List<String> categories = controller.getCategories();

        // Assert
        assertEquals(MessageCategory.values().length, categories.size());
        assertTrue(categories.contains(MessageCategory.FINANCE.name()));
    }

}