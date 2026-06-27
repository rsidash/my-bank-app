package ru.yandex.practicum.notifications.event;

public record AccountUpdatedEvent(String login, String name) {
}
