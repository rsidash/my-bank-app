package ru.yandex.practicum.notifications.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.notifications.service.NotificationService;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationKafkaListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = "notifications.account-updated", groupId = "notifications-group")
    public void onAccountUpdated(Map<String, Object> message) {
        String login = (String) message.get("login");
        String name = (String) message.get("name");
        notificationService.send(login, "Данные аккаунта обновлены. Новое имя: " + name);
    }

    @KafkaListener(topics = "notifications.transfer", groupId = "notifications-group")
    public void onTransfer(Map<String, Object> message) {
        String fromLogin = (String) message.get("fromLogin");
        String toLogin = (String) message.get("toLogin");
        int value = (int) message.get("value");
        notificationService.send(fromLogin, "Перевод %d руб. пользователю '%s'".formatted(value, toLogin));
        notificationService.send(toLogin, "Получен перевод %d руб. от пользователя '%s'".formatted(value, fromLogin));
    }

    @KafkaListener(topics = "notifications.cash-deposit", groupId = "notifications-group")
    public void onCashDeposit(Map<String, Object> message) {
        String login = (String) message.get("login");
        int value = (int) message.get("value");
        notificationService.send(login, "Пополнение счёта на %d руб.".formatted(value));
    }

    @KafkaListener(topics = "notifications.cash-withdraw", groupId = "notifications-group")
    public void onCashWithdraw(Map<String, Object> message) {
        String login = (String) message.get("login");
        int value = (int) message.get("value");
        notificationService.send(login, "Снятие со счёта %d руб.".formatted(value));
    }
}
