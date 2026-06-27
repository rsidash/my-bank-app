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
        notificationService.handleAccountUpdated(event);
    }

    @KafkaListener(topics = KafkaTopics.TRANSFER, groupId = "notifications-group")
    public void onTransfer(TransferEvent event) {
        notificationService.handleTransfer(event);
    }

    @KafkaListener(topics = KafkaTopics.CASH_DEPOSIT, groupId = "notifications-group")
    public void onCashDeposit(CashDepositEvent event) {
        notificationService.handleCashDeposit(event);
    }

    @KafkaListener(topics = KafkaTopics.CASH_WITHDRAW, groupId = "notifications-group")
    public void onCashWithdraw(CashWithdrawEvent event) {
        notificationService.handleCashWithdraw(event);
    }
}
