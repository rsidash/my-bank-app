package ru.yandex.practicum.mybankfront.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.yandex.practicum.mybankfront.config.GatewayProperties;
import ru.yandex.practicum.mybankfront.controller.dto.AccountDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class AccountsClient {

    private final RestClient restClient;

    public AccountsClient(GatewayProperties gatewayProperties, RestClient.Builder builder) {
        this.restClient = builder.baseUrl(gatewayProperties.getUrl() + "/api/accounts").build();
    }

    public Map<String, Object> getAccount(String login, String token) {
        return restClient.get()
                .uri("/{login}", login)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public Map<String, Object> updateAccount(String login, String name, LocalDate birthdate, String token) {
        return restClient.post()
                .uri("/{login}", login)
                .header("Authorization", "Bearer " + token)
                .body(Map.of("name", name, "birthdate", birthdate.toString()))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public List<AccountDto> getOtherAccounts(String login, String token) {
        return restClient.get()
                .uri("/{login}/others", login)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}
