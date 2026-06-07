package ru.yandex.practicum.mybankfront.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

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
}
