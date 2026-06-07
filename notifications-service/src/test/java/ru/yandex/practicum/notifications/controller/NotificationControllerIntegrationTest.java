package ru.yandex.practicum.notifications.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.notifications.config.TestSecurityConfig;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class NotificationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void accountUpdated_withNotificationsScope_returns200() throws Exception {
        mockMvc.perform(post("/notifications/account-updated")
                        .with(jwt().jwt(j -> j.claim("scope", "notifications")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"login": "ivanov", "name": "Новое Имя"}
                        """))
                .andExpect(status().isOk());
    }

    @Test
    void transfer_withNotificationsScope_returns200() throws Exception {
        mockMvc.perform(post("/notifications/transfer")
                        .with(jwt().jwt(j -> j.claim("scope", "notifications")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"fromLogin": "ivanov", "toLogin": "petrov", "value": 50}
                        """))
                .andExpect(status().isOk());
    }

    @Test
    void cashDeposit_withNotificationsScope_returns200() throws Exception {
        mockMvc.perform(post("/notifications/cash-deposit")
                        .with(jwt().jwt(j -> j.claim("scope", "notifications")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"login": "ivanov", "value": 100}
                        """))
                .andExpect(status().isOk());
    }

    @Test
    void cashWithdraw_withNotificationsScope_returns200() throws Exception {
        mockMvc.perform(post("/notifications/cash-withdraw")
                        .with(jwt().jwt(j -> j.claim("scope", "notifications")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"login": "ivanov", "value": 50}
                        """))
                .andExpect(status().isOk());
    }

    @Test
    void request_withoutScope_returns403() throws Exception {
        mockMvc.perform(post("/notifications/account-updated")
                        .with(jwt().jwt(j -> j.claim("scope", "other")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"login": "ivanov", "name": "Test"}
                        """))
                .andExpect(status().isForbidden());
    }

    @Test
    void request_unauthenticated_returnsForbidden() throws Exception {
        mockMvc.perform(post("/notifications/account-updated")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"login": "ivanov", "name": "Test"}
                        """))
                .andExpect(status().isForbidden());
    }
}
