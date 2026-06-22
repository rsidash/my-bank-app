package ru.yandex.practicum.transfer.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.transfer.event.KafkaTopics;
import ru.yandex.practicum.transfer.event.TransferEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationsClient {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void notifyTransfer(String fromLogin, String toLogin, int value) {
        kafkaTemplate.send(KafkaTopics.TRANSFER, fromLogin, new TransferEvent(fromLogin, toLogin, value))
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.warn("Failed to send transfer notification for '{}': {}", fromLogin, ex.getMessage(), ex);
                    }
                });
    }
}
