package ru.yandex.practicum.accounts.contract;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.accounts.config.TestSecurityConfig;
import ru.yandex.practicum.accounts.model.Account;
import ru.yandex.practicum.accounts.repository.AccountRepository;

import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@WithMockUser
@Import(TestSecurityConfig.class)
public class BaseContractTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(
                MockMvcBuilders.webAppContextSetup(context)
                        .apply(SecurityMockMvcConfigurers.springSecurity())
                        .defaultRequest(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/")
                                .with(csrf()).with(user("test")))
                        .build()
        );

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
}
