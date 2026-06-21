package ru.yandex.practicum.cash.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.cash.client.AccountsClient;
import ru.yandex.practicum.cash.config.TestSecurityConfig;

import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class CashControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountsClient accountsClient;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    @WithMockUser
    void processCash_deposit_returnsUpdatedAccount() throws Exception {
        when(accountsClient.deposit("ivanov", 100)).thenReturn(Map.of("login", "ivanov", "sum", 200));

        mockMvc.perform(post("/cash/ivanov").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"value": 100, "action": "PUT"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum").value(200));
    }

    @Test
    @WithMockUser
    void processCash_withdraw_returnsUpdatedAccount() throws Exception {
        when(accountsClient.withdraw("ivanov", 30)).thenReturn(Map.of("login", "ivanov", "sum", 70));

        mockMvc.perform(post("/cash/ivanov").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"value": 30, "action": "GET"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum").value(70));
    }

    @Test
    @WithMockUser
    void processCash_invalidAction_returns400() throws Exception {
        mockMvc.perform(post("/cash/ivanov").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"value": 100, "action": "INVALID"}
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unauthorizedRequest_returns401() throws Exception {
        mockMvc.perform(post("/cash/ivanov").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"value": 100, "action": "PUT"}
                        """))
                .andExpect(status().isUnauthorized());
    }
}
