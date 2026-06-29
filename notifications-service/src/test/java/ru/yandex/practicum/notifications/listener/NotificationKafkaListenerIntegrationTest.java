package ru.yandex.practicum.notifications.listener;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.core.event.AccountUpdatedEvent;
import ru.yandex.practicum.core.event.CashDepositEvent;
import ru.yandex.practicum.core.event.CashWithdrawEvent;
import ru.yandex.practicum.core.event.TransferEvent;
import ru.yandex.practicum.notifications.service.NotificationService;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.verify;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {
        "notifications.account-updated",
        "notifications.transfer",
        "notifications.cash-deposit",
        "notifications.cash-withdraw"
})
@DirtiesContext
class NotificationKafkaListenerIntegrationTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @MockBean
    private NotificationService notificationService;

    @Test
    void onAccountUpdated_consumesMessage() {
        var event = new AccountUpdatedEvent("ivanov", "Новое Имя");
        kafkaTemplate.send("notifications.account-updated", "ivanov", event);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() ->
                verify(notificationService).handleAccountUpdated(event));
    }

    @Test
    void onTransfer_consumesMessage() {
        var event = new TransferEvent("ivanov", "petrov", 50);
        kafkaTemplate.send("notifications.transfer", "ivanov", event);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() ->
                verify(notificationService).handleTransfer(event));
    }

    @Test
    void onCashDeposit_consumesMessage() {
        var event = new CashDepositEvent("ivanov", 100);
        kafkaTemplate.send("notifications.cash-deposit", "ivanov", event);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() ->
                verify(notificationService).handleCashDeposit(event));
    }

    @Test
    void onCashWithdraw_consumesMessage() {
        var event = new CashWithdrawEvent("ivanov", 50);
        kafkaTemplate.send("notifications.cash-withdraw", "ivanov", event);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() ->
                verify(notificationService).handleCashWithdraw(event));
    }
}
