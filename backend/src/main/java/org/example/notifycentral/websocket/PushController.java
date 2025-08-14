package org.example.notifycentral.websocket;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/push")
public class PushController {

    private final PushNotificationService pushService;
    private final List<Map<String, Object>> subscriptions = new ArrayList<>();

    public PushController(PushNotificationService pushService) {
        this.pushService = pushService;
    }

    @PostMapping("/subscribe")
    public void subscribe(@RequestBody Map<String, Object> subscription) {
        subscriptions.add(subscription);
    }

    @PostMapping("/send")
    public void send(@RequestBody Map<String, String> payload) throws Exception {
        for (Map<String, Object> sub : subscriptions) {
            pushService.sendPush(
                    new org.jose4j.json.internal.json_simple.JSONObject(sub),
                    payload.get("title"),
                    payload.get("body")
            );
        }
    }
}
