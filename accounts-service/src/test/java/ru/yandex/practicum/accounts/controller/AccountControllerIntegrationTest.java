package ru.yandex.practicum.accounts.controller;

import org.junit.jupiter.api.BeforeEach;
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
import ru.yandex.practicum.accounts.config.TestSecurityConfig;
import ru.yandex.practicum.accounts.model.Account;
import ru.yandex.practicum.accounts.repository.AccountRepository;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @BeforeEach
    void setUp() {
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(CompletableFuture.completedFuture(null));
        accountRepository.deleteAll();

        Account ivanov = new Account();
        ivanov.setLogin("ivanov");
        ivanov.setName("Иванов Иван");
        ivanov.setBirthdate(LocalDate.of(2001, 1, 1));
        ivanov.setSum(100);
        accountRepository.save(ivanov);

        Account petrov = new Account();
        petrov.setLogin("petrov");
        petrov.setName("Петров Петр");
        petrov.setBirthdate(LocalDate.of(1995, 5, 15));
        petrov.setSum(200);
        accountRepository.save(petrov);
    }

    @Test
    @WithMockUser
    void getAccount_returnsAccountData() throws Exception {
        mockMvc.perform(get("/accounts/ivanov"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.login", is("ivanov")))
                .andExpect(jsonPath("$.name", is("Иванов Иван")))
                .andExpect(jsonPath("$.sum", is(100)));
    }

    @Test
    @WithMockUser
    void getAccount_notFound_returns404() throws Exception {
        mockMvc.perform(get("/accounts/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @WithMockUser
    void updateAccount_updatesData() throws Exception {
        mockMvc.perform(post("/accounts/ivanov").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"name": "Новое Имя", "birthdate": "2000-06-15"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Новое Имя")))
                .andExpect(jsonPath("$.birthdate", is("2000-06-15")));
    }

    @Test
    @WithMockUser
    void getOtherAccounts_excludesCurrent() throws Exception {
        mockMvc.perform(get("/accounts/ivanov/others"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].login", is("petrov")));
    }

    @Test
    @WithMockUser
    void deposit_increasesBalance() throws Exception {
        mockMvc.perform(post("/accounts/ivanov/deposit").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"amount": 50}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum", is(150)));
    }

    @Test
    @WithMockUser
    void withdraw_sufficientFunds_decreasesBalance() throws Exception {
        mockMvc.perform(post("/accounts/ivanov/withdraw").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"amount": 30}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum", is(70)));
    }

    @Test
    @WithMockUser
    void withdraw_insufficientFunds_returns400() throws Exception {
        mockMvc.perform(post("/accounts/ivanov/withdraw").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"amount": 500}
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Недостаточно средств на счету"));
    }

    @Test
    @WithMockUser
    void transfer_movesMoneyBetweenAccounts() throws Exception {
        mockMvc.perform(post("/accounts/ivanov/transfer").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"toLogin": "petrov", "value": 50}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sum", is(50)));
    }

    @Test
    void unauthorizedRequest_returns401() throws Exception {
        mockMvc.perform(get("/accounts/ivanov"))
                .andExpect(status().isUnauthorized());
    }
}
