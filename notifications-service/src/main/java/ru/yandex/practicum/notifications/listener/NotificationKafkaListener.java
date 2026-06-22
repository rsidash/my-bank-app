package ru.yandex.practicum.notifications.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.notifications.event.AccountUpdatedEvent;
import ru.yandex.practicum.notifications.event.CashDepositEvent;
import ru.yandex.practicum.notifications.event.CashWithdrawEvent;
import ru.yandex.practicum.notifications.event.KafkaTopics;
import ru.yandex.practicum.notifications.event.TransferEvent;
import ru.yandex.practicum.notifications.service.NotificationService;

@Component
@RequiredArgsConstructor
public class NotificationKafkaListener {

    private final NotificationService notificationService;

    @KafkaListener(topics = KafkaTopics.ACCOUNT_UPDATED, groupId = "notifications-group")
    public void onAccountUpdated(AccountUpdatedEvent event) {
        notificationService.send(event.login(), "Данные аккаунта обновлены. Новое имя: " + event.name());
    }

    @KafkaListener(topics = KafkaTopics.TRANSFER, groupId = "notifications-group")
    public void onTransfer(TransferEvent event) {
        notificationService.send(event.fromLogin(), "Перевод %d руб. пользователю '%s'".formatted(event.value(), event.toLogin()));
        notificationService.send(event.toLogin(), "Получен перевод %d руб. от пользователя '%s'".formatted(event.value(), event.fromLogin()));
    }

    @KafkaListener(topics = KafkaTopics.CASH_DEPOSIT, groupId = "notifications-group")
    public void onCashDeposit(CashDepositEvent event) {
        notificationService.send(event.login(), "Пополнение счёта на %d руб.".formatted(event.value()));
    }

    @KafkaListener(topics = KafkaTopics.CASH_WITHDRAW, groupId = "notifications-group")
    public void onCashWithdraw(CashWithdrawEvent event) {
        notificationService.send(event.login(), "Снятие со счёта %d руб.".formatted(event.value()));
    }
}
