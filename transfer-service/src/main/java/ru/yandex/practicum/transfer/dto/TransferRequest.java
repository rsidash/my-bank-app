package ru.yandex.practicum.transfer.dto;

public record TransferRequest(String fromLogin, String toLogin, int value) {
}
