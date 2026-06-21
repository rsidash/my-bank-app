package ru.yandex.practicum.accounts.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationsClient {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void notifyAccountUpdated(String login, String name) {
        try {
            kafkaTemplate.send("notifications.account-updated",
                    login, Map.of("login", login, "name", name));
        } catch (Exception e) {
            log.warn("Failed to send account update notification: {}", e.getMessage(), e);
        }
    }
}
