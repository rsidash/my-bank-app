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
        var future = kafkaTemplate.send("notifications.cash-deposit", login, Map.of("login", login, "value", value));
        if (future != null) {
            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.warn("Failed to send cash deposit notification for '{}': {}", login, ex.getMessage(), ex);
                }
            });
        }
    }

    public void notifyCashWithdraw(String login, int value) {
        var future = kafkaTemplate.send("notifications.cash-withdraw", login, Map.of("login", login, "value", value));
        if (future != null) {
            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.warn("Failed to send cash withdraw notification for '{}': {}", login, ex.getMessage(), ex);
                }
            });
        }
    }
}
