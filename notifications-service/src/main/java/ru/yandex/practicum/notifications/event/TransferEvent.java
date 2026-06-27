package ru.yandex.practicum.notifications.event;

public record TransferEvent(String fromLogin, String toLogin, int value) {
}
