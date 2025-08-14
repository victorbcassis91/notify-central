package org.example.notifycentral.async.kafka;

import org.example.notifycentral.entity.NotificationLog;
import org.example.notifycentral.model.MessageCategory;
import org.example.notifycentral.repository.NotificationLogRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@TestPropertySource(properties = {
        "notifycentral.notification-type=kafka",
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(
        partitions = 1,
        topics = {"notification-messages", "notification-messages.DLQ"},
        brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"}
)
@EnableKafka
class NotificationKafkaIT {

    @Autowired
    private NotificationKafkaProducer notificationKafkaProducer;

    @Autowired
    private NotificationLogRepository logRepository;

    @Test
    void testSendAndConsumeNotification() throws InterruptedException {
        // Act
        String statusOrder = "statusOrder:" + UUID.randomUUID();
        notificationKafkaProducer.send(MessageCategory.FINANCE, "message", statusOrder);
        Thread.sleep(3000); // ait for consumption (KafkaListener normally triggers asynchronously)


        // Assert
        List<NotificationLog> notificationLogs = logRepository.findAll();
        assertThat(notificationLogs.size()).isEqualTo(4);
    }

}