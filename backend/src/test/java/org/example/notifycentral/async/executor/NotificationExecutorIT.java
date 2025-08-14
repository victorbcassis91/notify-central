package org.example.notifycentral.async.executor;

import org.example.notifycentral.entity.NotificationLog;
import org.example.notifycentral.model.MessageCategory;
import org.example.notifycentral.repository.NotificationLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
class NotificationExecutorIT {

    @Autowired
    private NotificationDispatcher dispatcher;

    @Autowired
    private NotificationLogRepository logRepository;

    @Test
    void dispatch_shouldSendNotificationAndPersistLog() {
        // Act
        String statusOrder = "statusOrder:" + UUID.randomUUID();
        dispatcher.dispatch(MessageCategory.FINANCE, "Test message", statusOrder);

        // Assert
        List<NotificationLog> notificationLogs = logRepository.findAll();
        assertThat(notificationLogs.size()).isEqualTo(4);
    }

}