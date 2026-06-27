package ru.yandex.practicum.cash.event;

public record CashDepositEvent(String login, int value) {
}
