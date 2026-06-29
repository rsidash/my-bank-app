package ru.yandex.practicum.transfer.service;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.transfer.client.AccountsClient;
import ru.yandex.practicum.transfer.client.NotificationsClient;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceTest {

    @Mock
    private AccountsClient accountsClient;

    @Mock
    private NotificationsClient notificationsClient;

    private TransferService transferService;

    @BeforeEach
    void setUp() {
        transferService = new TransferService(accountsClient, notificationsClient, new SimpleMeterRegistry());
    }

    @Test
    void transfer_callsAccountsAndSendsKafkaNotification() {
        Map<String, Object> expected = Map.of("login", "ivanov", "sum", 50);
        when(accountsClient.transfer("ivanov", "petrov", 50)).thenReturn(expected);

        Map<String, Object> result = transferService.transfer("ivanov", "petrov", 50);

        assertThat(result.get("sum")).isEqualTo(50);
        verify(accountsClient).transfer("ivanov", "petrov", 50);
        verify(notificationsClient).notifyTransfer("ivanov", "petrov", 50);
    }
}
