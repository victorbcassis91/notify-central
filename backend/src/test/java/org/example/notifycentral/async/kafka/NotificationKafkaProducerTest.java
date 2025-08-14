package org.example.notifycentral.async.kafka;

import org.example.notifycentral.dto.MessageRequest;
import org.example.notifycentral.model.MessageCategory;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

class NotificationKafkaProducerTest {

    @Test
    void send_shouldSendMessageToKafka() {
        KafkaTemplate<String, MessageRequest> kafkaTemplate = mock(KafkaTemplate.class);
        NotificationKafkaProducer producer = new NotificationKafkaProducer(kafkaTemplate);

        String statusOrder = "statusOrder:" + UUID.randomUUID();
        producer.send(MessageCategory.FINANCE, "Test message", statusOrder);

        ArgumentCaptor<MessageRequest> captor = ArgumentCaptor.forClass(MessageRequest.class);
        verify(kafkaTemplate, times(1)).send(eq("notification-messages"), captor.capture());

        MessageRequest sentMessage = captor.getValue();
        assertThat(sentMessage.getCategory()).isEqualTo(MessageCategory.FINANCE);
        assertThat(sentMessage.getMessage()).isEqualTo("Test message");
    }
}