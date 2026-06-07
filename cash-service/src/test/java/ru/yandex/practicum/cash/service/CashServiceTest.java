package ru.yandex.practicum.cash.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.cash.client.AccountsClient;
import ru.yandex.practicum.cash.client.NotificationsClient;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CashServiceTest {

    @Mock
    private AccountsClient accountsClient;

    @Mock
    private NotificationsClient notificationsClient;

    @InjectMocks
    private CashService cashService;

    @Test
    void deposit_callsAccountsAndNotifications() {
        Map<String, Object> expected = Map.of("login", "ivanov", "sum", 150);
        when(accountsClient.deposit("ivanov", 50)).thenReturn(expected);

        Map<String, Object> result = cashService.deposit("ivanov", 50);

        assertThat(result.get("sum")).isEqualTo(150);
        verify(accountsClient).deposit("ivanov", 50);
        verify(notificationsClient).notifyCashDeposit("ivanov", 50);
    }

    @Test
    void withdraw_callsAccountsAndNotifications() {
        Map<String, Object> expected = Map.of("login", "ivanov", "sum", 70);
        when(accountsClient.withdraw("ivanov", 30)).thenReturn(expected);

        Map<String, Object> result = cashService.withdraw("ivanov", 30);

        assertThat(result.get("sum")).isEqualTo(70);
        verify(accountsClient).withdraw("ivanov", 30);
        verify(notificationsClient).notifyCashWithdraw("ivanov", 30);
    }
}
