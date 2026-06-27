package ru.yandex.practicum.notifications.event;

public record CashDepositEvent(String login, int value) {
}
