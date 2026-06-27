package ru.yandex.practicum.accounts.event;

public record AccountUpdatedEvent(String login, String name) {
}
