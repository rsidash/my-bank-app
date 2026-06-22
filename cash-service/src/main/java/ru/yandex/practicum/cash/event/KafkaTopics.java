package ru.yandex.practicum.cash.event;

public interface KafkaTopics {
    String CASH_DEPOSIT = "notifications.cash-deposit";
    String CASH_WITHDRAW = "notifications.cash-withdraw";
}
