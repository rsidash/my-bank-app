package ru.yandex.practicum.accounts.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.yandex.practicum.accounts.config.NotificationsServiceProperties;

import java.util.Map;

@Slf4j
@Component
public class NotificationsClient {

    private final RestClient restClient;
    private final OAuth2AuthorizedClientManager clientManager;

    public NotificationsClient(
            NotificationsServiceProperties properties,
            RestClient.Builder builder,
            OAuth2AuthorizedClientService authorizedClientService,
            ClientRegistrationRepository clientRegistrationRepository) {
        this.restClient = builder.baseUrl(properties.getUrl()).build();
        var manager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientService);
        manager.setAuthorizedClientProvider(
                OAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().build());
        this.clientManager = manager;
    }

    public void notifyAccountUpdated(String login, String name) {
        try {
            String token = getClientCredentialsToken();
            restClient.post()
                    .uri("/account-updated")
                    .header("Authorization", "Bearer " + token)
                    .body(Map.of("login", login, "name", name))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Failed to send account update notification: {}", e.getMessage(), e);
        }
    }

    private String getClientCredentialsToken() {
        var request = OAuth2AuthorizeRequest
                .withClientRegistrationId("notifications")
                .principal("accounts-service")
                .build();
        var client = clientManager.authorize(request);
        return client.getAccessToken().getTokenValue();
    }
}
