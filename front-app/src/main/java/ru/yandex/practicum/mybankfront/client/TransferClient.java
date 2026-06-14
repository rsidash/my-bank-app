package ru.yandex.practicum.mybankfront.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.yandex.practicum.mybankfront.config.TransferServiceProperties;

import java.util.Map;

@Component
public class TransferClient {

    private final RestClient restClient;

    public TransferClient(TransferServiceProperties properties, RestClient.Builder builder) {
        this.restClient = builder.baseUrl(properties.getUrl()).build();
    }

    public Map<String, Object> transfer(String fromLogin, String toLogin, int value, String token) {
        return restClient.post()
                .header("Authorization", "Bearer " + token)
                .body(Map.of("fromLogin", fromLogin, "toLogin", toLogin, "value", value))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
