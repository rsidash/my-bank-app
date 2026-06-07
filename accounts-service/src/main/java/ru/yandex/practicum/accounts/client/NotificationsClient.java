package ru.yandex.practicum.accounts.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
public class NotificationsClient {

    private final RestClient restClient;
    private final OAuth2AuthorizedClientService authorizedClientService;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public NotificationsClient(
            @Value("${gateway.url}") String gatewayUrl,
            RestClient.Builder builder,
            OAuth2AuthorizedClientService authorizedClientService,
            ClientRegistrationRepository clientRegistrationRepository) {
        this.restClient = builder.baseUrl(gatewayUrl + "/api/notifications").build();
        this.authorizedClientService = authorizedClientService;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    public void notifyAccountUpdated(String login, String name) {
        String token = getClientCredentialsToken();
        restClient.post()
                .uri("/account-updated")
                .header("Authorization", "Bearer " + token)
                .body(Map.of("login", login, "name", name))
                .retrieve()
                .toBodilessEntity();
    }

    private String getClientCredentialsToken() {
        var clientRegistration = clientRegistrationRepository.findByRegistrationId("notifications");
        var clientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientService);
        clientManager.setAuthorizedClientProvider(
                OAuth2AuthorizedClientProviderBuilder.builder().clientCredentials().build());
        var request = OAuth2AuthorizeRequest
                .withClientRegistrationId("notifications")
                .principal("accounts-service")
                .build();
        var client = clientManager.authorize(request);
        return client.getAccessToken().getTokenValue();
    }
}
