package ru.yandex.practicum.transfer.event;

public record TransferEvent(String fromLogin, String toLogin, int value) {
}
