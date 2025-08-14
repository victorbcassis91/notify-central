package org.example.notifycentral.async.executor;

import lombok.RequiredArgsConstructor;
import org.example.notifycentral.entity.NotificationLog;
import org.example.notifycentral.entity.UserMock;
import org.example.notifycentral.model.MessageCategory;
import org.example.notifycentral.model.NotificationChannel;
import org.example.notifycentral.model.StatusOrder;
import org.example.notifycentral.redis.RedisService;
import org.example.notifycentral.repository.NotificationLogRepository;
import org.example.notifycentral.service.NotificationSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@EnableAsync
public class AsyncNotificationService {

    private final NotificationLogRepository notificationLogRepository;
    private final Map<String, NotificationSender> senders;
    private final RedisService redisService;

    @Value("${notifyhub.retry.max-attempts:3}")
    protected int maxAttempts;

    @Value("${notifyhub.retry.initial-backoff-ms:200}")
    protected long initialBackoffMs;

    public void sendWithRetry(UserMock userMock, MessageCategory category, NotificationChannel channel, String message, String statusOrder) {
        int attempt = 0;
        boolean delivered = false;
        String details = "";
        NotificationSender sender = senders.get(channel.name());

        while (attempt < maxAttempts && !delivered) {
            try {
                attempt++;
                if (sender == null) {
                    details = "Sender for channel " + channel + " not configured";
                    break;
                }
                NotificationSender.NotificationResult result = sender.send(userMock, message);
                delivered = result.success;
                details = result.details;

                if (!delivered) {
                    // wait before retry
                    Thread.sleep((long) (initialBackoffMs * Math.pow(2, attempt - 1)));
                }
            } catch (Exception e) {
                details = "Error sending notification: " + e.getMessage();
                try {
                    Thread.sleep((long) (initialBackoffMs * Math.pow(2, attempt - 1)));
                } catch (InterruptedException ignored) {
                }
            }
        }

        NotificationLog log = NotificationLog.builder()
                .category(category)
                .channel(channel)
                .userId(userMock.getId().toString())
                .userName(userMock.getName())
                .message(message)
                .delivered(delivered)
                .details(details)
                .timestamp(OffsetDateTime.now())
                .build();

        notificationLogRepository.save(log);

        redisService.setValue(statusOrder, delivered ? StatusOrder.SUCCESS.name() : StatusOrder.ERROR.name(), 6000);
    }

}
