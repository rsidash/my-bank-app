package ru.yandex.practicum.accounts.contract;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.context.WebApplicationContext;
import ru.yandex.practicum.accounts.client.NotificationsClient;
import ru.yandex.practicum.accounts.config.TestSecurityConfig;
import ru.yandex.practicum.accounts.model.Account;
import ru.yandex.practicum.accounts.repository.AccountRepository;

import java.time.LocalDate;

@SpringBootTest
@WithMockUser
@Import(TestSecurityConfig.class)
public class BaseContractTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    private NotificationsClient notificationsClient;

    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.webAppContextSetup(context);

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
