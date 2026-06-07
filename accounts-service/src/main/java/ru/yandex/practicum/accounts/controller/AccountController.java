package ru.yandex.practicum.accounts.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.accounts.client.NotificationsClient;
import ru.yandex.practicum.accounts.dto.UpdateAccountRequest;
import ru.yandex.practicum.accounts.model.Account;
import ru.yandex.practicum.accounts.service.AccountService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final NotificationsClient notificationsClient;

    @GetMapping("/{login}")
    public Account getAccount(@PathVariable String login) {
        return accountService.getAccount(login);
    }

    @PostMapping("/{login}")
    public Account updateAccount(@PathVariable String login, @RequestBody UpdateAccountRequest request) {
        Account account = accountService.updateAccount(login, request.name(), request.birthdate());
        try {
            notificationsClient.notifyAccountUpdated(login, request.name());
        } catch (Exception ignored) {
        }
        return account;
    }

    @GetMapping("/{login}/others")
    public List<Map<String, String>> getOtherAccounts(@PathVariable String login) {
        return accountService.getOtherAccounts(login).stream()
                .map(a -> Map.of("login", a.getLogin(), "name", a.getName()))
                .toList();
    }

    @PostMapping("/{login}/transfer")
    public ResponseEntity<?> transfer(@PathVariable String login, @RequestBody Map<String, Object> request) {
        String toLogin = (String) request.get("toLogin");
        int value = (int) request.get("value");
        accountService.transfer(login, toLogin, value);
        return ResponseEntity.ok(accountService.getAccount(login));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(IllegalArgumentException e) {
        return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalStateException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
