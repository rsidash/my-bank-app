package ru.yandex.practicum.cash.service;

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

    public Map<String, Object> deposit(String login, int value) {
        Map<String, Object> result = accountsClient.deposit(login, value);
        notificationsClient.notifyCashDeposit(login, value);
        return result;
    }

    public Map<String, Object> withdraw(String login, int value) {
        Map<String, Object> result = accountsClient.withdraw(login, value);
        notificationsClient.notifyCashWithdraw(login, value);
        return result;
    }
}
