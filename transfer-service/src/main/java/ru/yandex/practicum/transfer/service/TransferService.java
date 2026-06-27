package ru.yandex.practicum.transfer.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.transfer.client.AccountsClient;
import ru.yandex.practicum.transfer.client.NotificationsClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransferService {

    private final AccountsClient accountsClient;
    private final NotificationsClient notificationsClient;
    private final MeterRegistry registry;

    private Counter transferCounter;
    private DistributionSummary transferAmountSummary;

    @PostConstruct
    public void initMetrics() {
        transferCounter = Counter.builder("transfers.total")
                .description("Total number of transfers").register(registry);
        transferAmountSummary = DistributionSummary.builder("transfers.amount")
                .description("Transfer amounts distribution").register(registry);
    }

    public Map<String, Object> transfer(String fromLogin, String toLogin, int value) {
        Map<String, Object> result = accountsClient.transfer(fromLogin, toLogin, value);
        notificationsClient.notifyTransfer(fromLogin, toLogin, value);
        transferCounter.increment();
        transferAmountSummary.record(value);
        return result;
    }
}
