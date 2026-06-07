package ru.yandex.practicum.cash.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.yandex.practicum.cash.config.OAuth2TokenService;

import java.util.Map;

@Slf4j
@Component
public class NotificationsClient {

    private final RestClient restClient;
    private final OAuth2TokenService tokenService;

    public NotificationsClient(@Value("${gateway.url}") String gatewayUrl,
                               RestClient.Builder builder,
                               OAuth2TokenService tokenService) {
        this.restClient = builder.baseUrl(gatewayUrl + "/api/notifications").build();
        this.tokenService = tokenService;
    }

    public void notifyCashDeposit(String login, int value) {
        try {
            String token = tokenService.getToken("notifications");
            restClient.post()
                    .uri("/cash-deposit")
                    .header("Authorization", "Bearer " + token)
                    .body(Map.of("login", login, "value", value))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Failed to send deposit notification: {}", e.getMessage());
        }
    }

    public void notifyCashWithdraw(String login, int value) {
        try {
            String token = tokenService.getToken("notifications");
            restClient.post()
                    .uri("/cash-withdraw")
                    .header("Authorization", "Bearer " + token)
                    .body(Map.of("login", login, "value", value))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Failed to send withdraw notification: {}", e.getMessage());
        }
    }
}
