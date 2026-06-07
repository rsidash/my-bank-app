package ru.yandex.practicum.cash.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.yandex.practicum.cash.config.OAuth2TokenService;

import java.util.Map;

@Slf4j
@Component
public class AccountsClient {

    private final RestClient restClient;
    private final OAuth2TokenService tokenService;

    public AccountsClient(@Value("${gateway.url}") String gatewayUrl,
                          RestClient.Builder builder,
                          OAuth2TokenService tokenService) {
        this.restClient = builder.baseUrl(gatewayUrl + "/api/accounts").build();
        this.tokenService = tokenService;
    }

    public Map<String, Object> deposit(String login, int amount) {
        String token = tokenService.getToken("accounts");
        return restClient.post()
                .uri("/{login}/deposit", login)
                .header("Authorization", "Bearer " + token)
                .body(Map.of("amount", amount))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public Map<String, Object> withdraw(String login, int amount) {
        String token = tokenService.getToken("accounts");
        return restClient.post()
                .uri("/{login}/withdraw", login)
                .header("Authorization", "Bearer " + token)
                .body(Map.of("amount", amount))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
