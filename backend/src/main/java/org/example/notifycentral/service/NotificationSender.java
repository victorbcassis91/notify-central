package org.example.notifycentral.service;

import org.example.notifycentral.entity.UserMock;

public interface NotificationSender {
    NotificationResult send(UserMock user, String message);

    class NotificationResult {

        public final boolean success;
        public final String details;

        public NotificationResult(boolean success, String details) {
            this.success = success;
            this.details = details;
        }
    }

}
