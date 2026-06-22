package ru.yandex.practicum.transfer.controller;

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
import ru.yandex.practicum.transfer.client.AccountsClient;
import ru.yandex.practicum.transfer.config.TestSecurityConfig;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class TransferControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountsClient accountsClient;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    @WithMockUser
    void transfer_success_returnsUpdatedAccount() throws Exception {
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(CompletableFuture.completedFuture(null));
        when(accountsClient.transfer("ivanov", "petrov", 50))
                .thenReturn(Map.of("login", "ivanov", "sum", 50));

        mockMvc.perform(post("/transfer").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"fromLogin": "ivanov", "toLogin": "petrov", "value": 50}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login").value("ivanov"))
                .andExpect(jsonPath("$.sum").value(50));
    }

    @Test
    void unauthorizedRequest_returns401() throws Exception {
        mockMvc.perform(post("/transfer").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"fromLogin": "ivanov", "toLogin": "petrov", "value": 50}
                        """))
                .andExpect(status().isUnauthorized());
    }
}
