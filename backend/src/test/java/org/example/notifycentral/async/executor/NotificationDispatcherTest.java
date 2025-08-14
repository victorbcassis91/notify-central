package org.example.notifycentral.async.executor;

import org.example.notifycentral.entity.UserMock;
import org.example.notifycentral.model.MessageCategory;
import org.example.notifycentral.model.NotificationChannel;
import org.example.notifycentral.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

import static org.mockito.Mockito.*;

class NotificationDispatcherTest {

    private UserService userService;
    private AsyncNotificationService asyncService;
    private Executor executor;
    private NotificationDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        asyncService = mock(AsyncNotificationService.class);

        executor = Runnable::run;

        dispatcher = new NotificationDispatcher(userService, asyncService, executor);
    }

    @Test
    void dispatch_shouldExecuteAsyncForEachChannelOfEachUser() {
        UserMock user = UserMock.builder()
                .name("Victor")
                .channels(List.of(NotificationChannel.EMAIL, NotificationChannel.SMS))
                .build();

        when(userService.findByCategory(MessageCategory.SPORTS)).thenReturn(List.of(user));

        String statusOrder = "statusOrder:" + UUID.randomUUID();
        dispatcher.dispatch(MessageCategory.SPORTS, "msg", statusOrder);

        verify(asyncService, times(1))
                .sendWithRetry(user, MessageCategory.SPORTS, NotificationChannel.EMAIL, "msg", statusOrder);

        verify(asyncService, times(1))
                .sendWithRetry(user, MessageCategory.SPORTS, NotificationChannel.SMS, "msg", statusOrder);
    }

}