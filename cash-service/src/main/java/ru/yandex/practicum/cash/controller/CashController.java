package ru.yandex.practicum.cash.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientResponseException;
import ru.yandex.practicum.cash.dto.CashRequest;
import ru.yandex.practicum.cash.service.CashService;

import java.util.Map;

@RestController
@RequestMapping("/cash")
@RequiredArgsConstructor
public class CashController {

    private final CashService cashService;

    @PostMapping("/{login}")
    public ResponseEntity<?> processCash(@PathVariable String login, @RequestBody CashRequest request) {
        try {
            Map<String, Object> result = switch (request.action()) {
                case "PUT" -> cashService.deposit(login, request.value());
                case "GET" -> cashService.withdraw(login, request.value());
                default -> throw new IllegalArgumentException("Неизвестное действие: " + request.action());
            };
            return ResponseEntity.ok(result);
        } catch (RestClientResponseException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("error", e.getResponseBodyAsString()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
