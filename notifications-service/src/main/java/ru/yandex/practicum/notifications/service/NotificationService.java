package ru.yandex.practicum.notifications.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    public void send(String login, String message) {
        log.info("[NOTIFICATION] Пользователь '{}': {}", login, message);
    }
}
