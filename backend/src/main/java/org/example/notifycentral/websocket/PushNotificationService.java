package org.example.notifycentral.websocket;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Service;

import java.security.Security;

@Service
public class PushNotificationService {

    private PushService pushService;

    public PushNotificationService() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        String publicKey = "BBiJP8WOhXZMRw87XqCOw3E7qeC2CJsww0vpgl0eFIHMCh1OV7itJV1cl-7UVMCiNUXQl8_HQ67CrrneWzkM40k";
        String privateKey = "G1LF7_JWCm7xwvFOLIMXdMqWLobe9PC8zsliCYSkkuY";
        pushService = new PushService(publicKey, privateKey);
    }

    public void sendPush(org.jose4j.json.internal.json_simple.JSONObject subscription, String title, String body) throws Exception {
        String endpoint = subscription.get("endpoint").toString();
        String p256dh = ((org.jose4j.json.internal.json_simple.JSONObject) subscription.get("keys")).get("p256dh").toString();
        String auth = ((org.jose4j.json.internal.json_simple.JSONObject) subscription.get("keys")).get("auth").toString();

        Notification notification = new Notification(endpoint, p256dh, auth,
                String.format("{\"title\":\"%s\",\"body\":\"%s\"}", title, body).getBytes());

        pushService.send(notification);
    }
}
