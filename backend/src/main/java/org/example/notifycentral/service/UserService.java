package org.example.notifycentral.service;

import jakarta.annotation.PostConstruct;
import org.example.notifycentral.entity.UserMock;
import org.example.notifycentral.model.MessageCategory;
import org.example.notifycentral.model.NotificationChannel;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private List<UserMock> users = new ArrayList<>();

    @PostConstruct
    public void init() {
        users = List.of(UserMock.builder()
                        .id(UUID.randomUUID())
                        .name("João")
                        .email("joao@example.com")
                        .phone("+5511999000111")
                        .subscribed(List.of(MessageCategory.SPORTS, MessageCategory.MOVIES))
                        .channels(List.of(NotificationChannel.EMAIL, NotificationChannel.PUSH))
                        .build(),
                UserMock.builder()
                        .id(UUID.randomUUID())
                        .name("Maria")
                        .email("maria@example.com")
                        .phone("+5511999000222")
                        .subscribed(List.of(MessageCategory.FINANCE, MessageCategory.MOVIES))
                        .channels(List.of(NotificationChannel.SMS, NotificationChannel.PUSH))
                        .build(),
                UserMock.builder()
                        .id(UUID.randomUUID())
                        .name("Jose")
                        .email("jose@example.com")
                        .phone("+5511999000333")
                        .subscribed(List.of(MessageCategory.SPORTS, MessageCategory.FINANCE))
                        .channels(List.of(NotificationChannel.EMAIL, NotificationChannel.SMS))
                        .build());
    }

    public List<UserMock> findAll() {
        return Collections.unmodifiableList(users);
    }

    public List<UserMock> findByCategory(MessageCategory category) {
        return users.stream().filter(u -> u.getSubscribed().contains(category)).toList();
    }


}
