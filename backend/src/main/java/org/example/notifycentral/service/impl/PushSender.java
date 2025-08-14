package org.example.notifycentral.service.impl;

import org.example.notifycentral.entity.UserMock;
import org.example.notifycentral.service.NotificationSender;
import org.springframework.stereotype.Component;

@Component("PUSH")
public class PushSender implements NotificationSender {

    @Override
    public NotificationResult send(UserMock user, String message) {
        String details = "Push sent to " + user.getName();
        return new NotificationResult(true, details);
    }
}
