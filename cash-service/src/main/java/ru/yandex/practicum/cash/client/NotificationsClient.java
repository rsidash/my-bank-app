package ru.yandex.practicum.cash.client;

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

    public void notifyCashDeposit(String login, int value) {
        try {
            kafkaTemplate.send("notifications.cash-deposit",
                    login, Map.of("login", login, "value", value));
        } catch (Exception e) {
            log.warn("Failed to send cash deposit notification: {}", e.getMessage(), e);
        }
    }

    public void notifyCashWithdraw(String login, int value) {
        try {
            kafkaTemplate.send("notifications.cash-withdraw",
                    login, Map.of("login", login, "value", value));
        } catch (Exception e) {
            log.warn("Failed to send cash withdraw notification: {}", e.getMessage(), e);
        }
    }
}
