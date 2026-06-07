package ru.yandex.practicum.mybankfront.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    private final OAuth2AuthorizedClientService authorizedClientService;

    public SecurityService(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    public String getCurrentUserLogin() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Пользователь не аутентифицирован");
        }
        var oauth2User = (OAuth2User) authentication.getPrincipal();
        String username = oauth2User.getAttribute("preferred_username");
        if (username == null) {
            username = oauth2User.getName();
        }
        return username;
    }

    public String getAccessToken() {
        var authentication = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
        );
        return client.getAccessToken().getTokenValue();
    }
}
