package ru.yandex.practicum.cash.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.cash.client.AccountsClient;
import ru.yandex.practicum.cash.client.NotificationsClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CashService {

    private final AccountsClient accountsClient;
    private final NotificationsClient notificationsClient;
    private final MeterRegistry registry;

    private Counter depositCounter;
    private Counter withdrawCounter;

    @PostConstruct
    public void initMetrics() {
        depositCounter = Counter.builder("cash.operations.total")
                .tag("type", "deposit").description("Total cash deposit operations").register(registry);
        withdrawCounter = Counter.builder("cash.operations.total")
                .tag("type", "withdraw").description("Total cash withdraw operations").register(registry);
    }

    public Map<String, Object> deposit(String login, int value) {
        Map<String, Object> result = accountsClient.deposit(login, value);
        notificationsClient.notifyCashDeposit(login, value);
        depositCounter.increment();
        return result;
    }

    public Map<String, Object> withdraw(String login, int value) {
        Map<String, Object> result = accountsClient.withdraw(login, value);
        notificationsClient.notifyCashWithdraw(login, value);
        withdrawCounter.increment();
        return result;
    }
}
