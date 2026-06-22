package ru.yandex.practicum.cash.event;

public record CashWithdrawEvent(String login, int value) {
}
