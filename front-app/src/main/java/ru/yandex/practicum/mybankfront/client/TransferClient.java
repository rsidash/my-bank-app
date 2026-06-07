package ru.yandex.practicum.mybankfront.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class TransferClient {

    private final RestClient restClient;

    public TransferClient(@Value("${gateway.url}") String gatewayUrl, RestClient.Builder builder) {
        this.restClient = builder.baseUrl(gatewayUrl + "/api/transfer").build();
    }

    public Map<String, Object> transfer(String fromLogin, String toLogin, int value, String token) {
        return restClient.post()
                .header("Authorization", "Bearer " + token)
                .body(Map.of("fromLogin", fromLogin, "toLogin", toLogin, "value", value))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
