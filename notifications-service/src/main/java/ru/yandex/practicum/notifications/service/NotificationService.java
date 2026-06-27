package ru.yandex.practicum.notifications.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.notifications.event.AccountUpdatedEvent;
import ru.yandex.practicum.notifications.event.CashDepositEvent;
import ru.yandex.practicum.notifications.event.CashWithdrawEvent;
import ru.yandex.practicum.notifications.event.TransferEvent;

@Slf4j
@Service
public class NotificationService {

    public void handleAccountUpdated(AccountUpdatedEvent event) {
        send(event.login(), "Данные аккаунта обновлены. Новое имя: " + event.name());
    }

    public void handleTransfer(TransferEvent event) {
        send(event.fromLogin(), "Перевод %d руб. пользователю '%s'".formatted(event.value(), event.toLogin()));
        send(event.toLogin(), "Получен перевод %d руб. от пользователя '%s'".formatted(event.value(), event.fromLogin()));
    }

    public void handleCashDeposit(CashDepositEvent event) {
        send(event.login(), "Пополнение счёта на %d руб.".formatted(event.value()));
    }

    public void handleCashWithdraw(CashWithdrawEvent event) {
        send(event.login(), "Снятие со счёта %d руб.".formatted(event.value()));
    }

    private void send(String login, String message) {
        log.info("[NOTIFICATION] Пользователь '{}': {}", login, message);
    }
}
