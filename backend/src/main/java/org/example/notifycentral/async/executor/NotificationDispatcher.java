package org.example.notifycentral.async.executor;

import lombok.RequiredArgsConstructor;
import org.example.notifycentral.entity.UserMock;
import org.example.notifycentral.model.MessageCategory;
import org.example.notifycentral.model.NotificationChannel;
import org.example.notifycentral.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.Executor;

@Service
@RequiredArgsConstructor
public class NotificationDispatcher {

    private final UserService userService;
    private final AsyncNotificationService asyncNotificationService;
    private final Executor notificationExecutor;

    @Transactional
    public void dispatch(MessageCategory category, String message, String statusOrder) {
        List<UserMock> subscribers = userService.findByCategory(category);

        for (UserMock user : subscribers) {
            for (NotificationChannel channel : user.getChannels()) {
                notificationExecutor.execute(() -> asyncNotificationService.sendWithRetry(user, category, channel, message, statusOrder));
            }
        }
    }

}
