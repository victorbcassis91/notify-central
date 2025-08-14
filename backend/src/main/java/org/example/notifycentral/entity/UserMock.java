package org.example.notifycentral.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.notifycentral.model.MessageCategory;
import org.example.notifycentral.model.NotificationChannel;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserMock {

    private UUID id;
    private String name;
    private String email;
    private String phone;
    private List<MessageCategory> subscribed;
    private List<NotificationChannel> channels;

}
