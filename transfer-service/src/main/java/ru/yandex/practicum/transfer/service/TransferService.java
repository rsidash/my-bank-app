package ru.yandex.practicum.transfer.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.transfer.client.AccountsClient;
import ru.yandex.practicum.transfer.client.NotificationsClient;

import java.util.Map;

@Service
public class TransferService {

    private final AccountsClient accountsClient;
    private final NotificationsClient notificationsClient;
    private final Counter transferCounter;
    private final DistributionSummary transferAmountSummary;

    public TransferService(AccountsClient accountsClient, NotificationsClient notificationsClient, MeterRegistry registry) {
        this.accountsClient = accountsClient;
        this.notificationsClient = notificationsClient;
        this.transferCounter = Counter.builder("transfers.total")
                .description("Total number of transfers").register(registry);
        this.transferAmountSummary = DistributionSummary.builder("transfers.amount")
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
