package ru.yandex.practicum.notifications.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.notifications.service.NotificationService;

import java.util.Map;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/account-updated")
    public ResponseEntity<Void> accountUpdated(@RequestBody Map<String, String> request) {
        String login = request.get("login");
        String name = request.get("name");
        notificationService.send(login, "Данные аккаунта обновлены. Новое имя: " + name);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(@RequestBody Map<String, Object> request) {
        String fromLogin = (String) request.get("fromLogin");
        String toLogin = (String) request.get("toLogin");
        int value = (int) request.get("value");
        notificationService.send(fromLogin, "Перевод %d руб. пользователю '%s'".formatted(value, toLogin));
        notificationService.send(toLogin, "Получен перевод %d руб. от пользователя '%s'".formatted(value, fromLogin));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cash-deposit")
    public ResponseEntity<Void> cashDeposit(@RequestBody Map<String, Object> request) {
        String login = (String) request.get("login");
        int value = (int) request.get("value");
        notificationService.send(login, "Пополнение счёта на %d руб.".formatted(value));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cash-withdraw")
    public ResponseEntity<Void> cashWithdraw(@RequestBody Map<String, Object> request) {
        String login = (String) request.get("login");
        int value = (int) request.get("value");
        notificationService.send(login, "Снятие со счёта %d руб.".formatted(value));
        return ResponseEntity.ok().build();
    }
}
