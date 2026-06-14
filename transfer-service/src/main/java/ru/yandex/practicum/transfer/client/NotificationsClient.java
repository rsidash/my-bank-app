package ru.yandex.practicum.transfer.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.yandex.practicum.transfer.config.GatewayProperties;
import ru.yandex.practicum.transfer.config.OAuth2TokenService;

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

    public void notifyTransfer(String fromLogin, String toLogin, int value) {
        try {
            String token = tokenService.getToken("notifications");
            restClient.post()
                    .uri("/transfer")
                    .header("Authorization", "Bearer " + token)
                    .body(Map.of("fromLogin", fromLogin, "toLogin", toLogin, "value", value))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Failed to send transfer notification: {}", e.getMessage(), e);
        }
    }
}
