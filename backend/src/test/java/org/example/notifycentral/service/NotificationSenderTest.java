package org.example.notifycentral.service;

import org.example.notifycentral.entity.UserMock;
import org.example.notifycentral.service.impl.EmailSender;
import org.example.notifycentral.service.impl.PushSender;
import org.example.notifycentral.service.impl.SmsSender;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationSenderTest {

    @Test
    void shouldSendEmailAndReturnSuccess() {
        // Arrange
        NotificationSender emailSender = new EmailSender();
        UserMock user = new UserMock();
        user.setEmail("test@example.com");

        String message = "Hello, this is a test.";

        // Act
        NotificationSender.NotificationResult result = emailSender.send(user, message);

        // Assert
        assertTrue(result.success, "The sending should return success");
        assertTrue(result.details.contains(user.getEmail()), "The details should contain the user's email");
        assertEquals("Email to test@example.com simulated", result.details);
    }

    @Test
    void shouldSendPushAndReturnSuccess() {
        // Arrange
        NotificationSender emailSender = new PushSender();
        UserMock user = new UserMock();
        user.setName("test");

        String message = "Hello, this is a test.";

        // Act
        NotificationSender.NotificationResult result = emailSender.send(user, message);

        // Assert
        assertTrue(result.success, "The sending should return success");
        assertTrue(result.details.contains(user.getName()), "The details should contain the user's name");
        assertEquals("Push sent to test", result.details);
    }

    @Test
    void shouldSendSmsAndReturnSuccess() {
        // Arrange
        NotificationSender emailSender = new SmsSender();
        UserMock user = new UserMock();
        user.setPhone("123456789");

        String message = "Hello, this is a test.";

        // Act
        NotificationSender.NotificationResult result = emailSender.send(user, message);

        // Assert
        assertTrue(result.success, "The sending should return success");
        assertTrue(result.details.contains(user.getPhone()), "The details should contain the user's phone");
        assertEquals("SMS to 123456789 queued", result.details);
    }

}