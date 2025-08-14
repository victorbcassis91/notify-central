package org.example.notifycentral.controller;

import lombok.RequiredArgsConstructor;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationDispatcher notificationDispatcher;
    private final NotificationKafkaProducer notificationKafkaProducer;
    private final NotificationLogRepository notificationLogRepository;
    private final RedisService redisService;

    @Value("${notifycentral.notification-type:executor}")
    private NotificationType notificationTyoe;

    @PostMapping("/messages")
    public String submitMessage(@Validated @RequestBody MessageRequest messageRequest) throws Exception {
        String statusOrder = "statusOrder:" + UUID.randomUUID();
        if (notificationTyoe.equals(NotificationType.EXECUTOR)) {
            redisService.setValue(statusOrder, StatusOrder.PENDING.name(), 600);
            notificationDispatcher.dispatch(messageRequest.getCategory(), messageRequest.getMessage(), statusOrder);
        } else if (notificationTyoe.equals(NotificationType.KAFKA)) {
            redisService.setValue(statusOrder, StatusOrder.PENDING.name(), 600);
            notificationKafkaProducer.send(messageRequest.getCategory(), messageRequest.getMessage(), statusOrder);
        } else {
            throw new Exception("There is not a correct notification type configured.");
        }

        return statusOrder;
    }

    @GetMapping("/notifications")
    public PageResponse<NotificationLog> getNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        List<NotificationLog> notificationLogs = notificationLogRepository.findAll().stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .toList();

        int totalElements = notificationLogs.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        int start = page * size;
        int end = Math.min(start + size, totalElements);

        List<NotificationLog> pageContent = start > totalElements
                ? List.of()
                : notificationLogs.subList(start, end);

        return new PageResponse<>(pageContent, totalElements, totalPages, page, size);
    }

    @GetMapping("/statusOrder/{STATUS_ORDER_ID}")
    public StatusOrder getStatusOrder(@PathVariable("STATUS_ORDER_ID") String statusOrderId) {
        return StatusOrder.valueOf(redisService.getValue(statusOrderId));
    }

    @GetMapping("/categories")
    public List<String> getCategories() {
        return Arrays.stream(MessageCategory.values())
                .map(Enum::name)
                .toList();
    }

}
