package ru.yandex.practicum.notifications.listener;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.notifications.event.AccountUpdatedEvent;
import ru.yandex.practicum.notifications.event.CashDepositEvent;
import ru.yandex.practicum.notifications.event.CashWithdrawEvent;
import ru.yandex.practicum.notifications.event.TransferEvent;
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
        kafkaTemplate.send("notifications.account-updated", "ivanov", new AccountUpdatedEvent("ivanov", "Новое Имя"));

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() ->
                verify(notificationService).send("ivanov", "Данные аккаунта обновлены. Новое имя: Новое Имя"));
    }

    @Test
    void onTransfer_consumesMessage() {
        kafkaTemplate.send("notifications.transfer", "ivanov", new TransferEvent("ivanov", "petrov", 50));

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(notificationService).send("ivanov", "Перевод 50 руб. пользователю 'petrov'");
            verify(notificationService).send("petrov", "Получен перевод 50 руб. от пользователя 'ivanov'");
        });
    }

    @Test
    void onCashDeposit_consumesMessage() {
        kafkaTemplate.send("notifications.cash-deposit", "ivanov", new CashDepositEvent("ivanov", 100));

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() ->
                verify(notificationService).send("ivanov", "Пополнение счёта на 100 руб."));
    }

    @Test
    void onCashWithdraw_consumesMessage() {
        kafkaTemplate.send("notifications.cash-withdraw", "ivanov", new CashWithdrawEvent("ivanov", 50));

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() ->
                verify(notificationService).send("ivanov", "Снятие со счёта 50 руб."));
    }
}
