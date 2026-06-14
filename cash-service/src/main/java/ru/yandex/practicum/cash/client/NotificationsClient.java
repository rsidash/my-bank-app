package ru.yandex.practicum.cash.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.yandex.practicum.cash.config.GatewayProperties;
import ru.yandex.practicum.cash.config.OAuth2TokenService;

import java.util.Map;

@Slf4j
@Component
public class NotificationsClient {

    private final RestClient restClient;
    private final OAuth2TokenService tokenService;

    public NotificationsClient(GatewayProperties gatewayProperties,
                               RestClient.Builder builder,
                               OAuth2TokenService tokenService) {
        this.restClient = builder.baseUrl(gatewayProperties.getUrl() + "/api/notifications").build();
        this.tokenService = tokenService;
    }

    public void notifyCashDeposit(String login, int value) {
        sendNotification("/cash-deposit", Map.of("login", login, "value", value));
    }

    public void notifyCashWithdraw(String login, int value) {
        sendNotification("/cash-withdraw", Map.of("login", login, "value", value));
    }

    private void sendNotification(String uri, Map<String, Object> body) {
        try {
            String token = tokenService.getToken("notifications");
            restClient.post()
                    .uri(uri)
                    .header("Authorization", "Bearer " + token)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Failed to send notification to {}: {}", uri, e.getMessage(), e);
        }
    }
}
