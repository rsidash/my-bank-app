package ru.yandex.practicum.transfer.service;

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

    public Map<String, Object> transfer(String fromLogin, String toLogin, int value) {
        Map<String, Object> result = accountsClient.transfer(fromLogin, toLogin, value);
        notificationsClient.notifyTransfer(fromLogin, toLogin, value);
        return result;
    }
}
