package ru.yandex.practicum.notifications.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.core.event.AccountUpdatedEvent;
import ru.yandex.practicum.core.event.CashDepositEvent;
import ru.yandex.practicum.core.event.CashWithdrawEvent;
import ru.yandex.practicum.core.event.TransferEvent;

@Slf4j
@Service
public class NotificationService {

    private final Counter accountUpdatedCounter;
    private final Counter transferCounter;
    private final Counter cashDepositCounter;
    private final Counter cashWithdrawCounter;

    public NotificationService(MeterRegistry registry) {
        this.accountUpdatedCounter = Counter.builder("notifications.processed.total")
                .tag("type", "account-updated").register(registry);
        this.transferCounter = Counter.builder("notifications.processed.total")
                .tag("type", "transfer").register(registry);
        this.cashDepositCounter = Counter.builder("notifications.processed.total")
                .tag("type", "cash-deposit").register(registry);
        this.cashWithdrawCounter = Counter.builder("notifications.processed.total")
                .tag("type", "cash-withdraw").register(registry);
    }

    public void handleAccountUpdated(AccountUpdatedEvent event) {
        send(event.login(), "Данные аккаунта обновлены. Новое имя: " + event.name());
        accountUpdatedCounter.increment();
    }

    public void handleTransfer(TransferEvent event) {
        send(event.fromLogin(), "Перевод %d руб. пользователю '%s'".formatted(event.value(), event.toLogin()));
        send(event.toLogin(), "Получен перевод %d руб. от пользователя '%s'".formatted(event.value(), event.fromLogin()));
        transferCounter.increment();
    }

    public void handleCashDeposit(CashDepositEvent event) {
        send(event.login(), "Пополнение счёта на %d руб.".formatted(event.value()));
        cashDepositCounter.increment();
    }

    public void handleCashWithdraw(CashWithdrawEvent event) {
        send(event.login(), "Снятие со счёта %d руб.".formatted(event.value()));
        cashWithdrawCounter.increment();
    }

    private void send(String login, String message) {
        log.info("[NOTIFICATION] Пользователь '{}': {}", login, message);
    }
}
