package org.example.notifycentral.service.impl;

import org.example.notifycentral.entity.UserMock;
import org.example.notifycentral.service.NotificationSender;
import org.springframework.stereotype.Component;

@Component("EMAIL")
public class EmailSender implements NotificationSender {

    @Override
    public NotificationResult send(UserMock user, String message) {
        String details = "Email to " + user.getEmail() + " simulated";
        return new NotificationResult(true, details);
    }
}
