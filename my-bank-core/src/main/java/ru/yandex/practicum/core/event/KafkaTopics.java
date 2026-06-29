package ru.yandex.practicum.core.event;

public interface KafkaTopics {
    String ACCOUNT_UPDATED = "notifications.account-updated";
    String TRANSFER = "notifications.transfer";
    String CASH_DEPOSIT = "notifications.cash-deposit";
    String CASH_WITHDRAW = "notifications.cash-withdraw";
}
