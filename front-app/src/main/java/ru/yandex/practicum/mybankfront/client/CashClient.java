package ru.yandex.practicum.mybankfront.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.yandex.practicum.mybankfront.config.GatewayProperties;

import java.util.Map;

@Component
public class CashClient {

    private final RestClient restClient;

    public CashClient(GatewayProperties gatewayProperties, RestClient.Builder builder) {
        this.restClient = builder.baseUrl(gatewayProperties.getUrl() + "/api/cash").build();
    }

    public Map<String, Object> processCash(String login, int value, String action, String token) {
        return restClient.post()
                .uri("/{login}", login)
                .header("Authorization", "Bearer " + token)
                .body(Map.of("value", value, "action", action))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
