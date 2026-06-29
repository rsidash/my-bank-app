package ru.yandex.practicum.core.event;

public record TransferEvent(String fromLogin, String toLogin, int value) {
}
