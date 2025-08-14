package org.example.notifycentral.service.impl;

import org.example.notifycentral.entity.UserMock;
import org.example.notifycentral.service.NotificationSender;
import org.springframework.stereotype.Component;

@Component("SMS")
public class SmsSender implements NotificationSender {

    @Override
    public NotificationResult send(UserMock user, String message) {
        String details = "SMS to " + user.getPhone() + " queued";
        return new NotificationResult(true, details);
    }
}
