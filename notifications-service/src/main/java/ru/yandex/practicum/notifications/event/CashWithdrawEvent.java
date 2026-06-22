package ru.yandex.practicum.notifications.event;

public record CashWithdrawEvent(String login, int value) {
}
