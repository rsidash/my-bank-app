package ru.yandex.practicum.transfer.client;

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

    public void notifyTransfer(String fromLogin, String toLogin, int value) {
        try {
            kafkaTemplate.send("notifications.transfer",
                    fromLogin, Map.of("fromLogin", fromLogin, "toLogin", toLogin, "value", value));
        } catch (Exception e) {
            log.warn("Failed to send transfer notification: {}", e.getMessage(), e);
        }
    }
}
