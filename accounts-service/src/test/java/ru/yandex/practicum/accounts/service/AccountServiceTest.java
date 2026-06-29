package ru.yandex.practicum.accounts.service;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.accounts.model.Account;
import ru.yandex.practicum.accounts.repository.AccountRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    private AccountService accountService;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(accountRepository, new SimpleMeterRegistry());

        testAccount = new Account();
        testAccount.setId(1L);
        testAccount.setLogin("ivanov");
        testAccount.setName("Иванов Иван");
        testAccount.setBirthdate(LocalDate.of(2001, 1, 1));
        testAccount.setSum(100);
    }

    @Test
    void getAccount_existingLogin_returnsAccount() {
        when(accountRepository.findByLogin("ivanov")).thenReturn(Optional.of(testAccount));

        Account result = accountService.getAccount("ivanov");

        assertThat(result.getLogin()).isEqualTo("ivanov");
        assertThat(result.getSum()).isEqualTo(100);
    }

    @Test
    void getAccount_nonExistingLogin_throwsException() {
        when(accountRepository.findByLogin("unknown")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getAccount("unknown"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Аккаунт не найден");
    }

    @Test
    void updateAccount_updatesNameAndBirthdate() {
        when(accountRepository.findByLogin("ivanov")).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any())).thenReturn(testAccount);

        Account result = accountService.updateAccount("ivanov", "Новое Имя", LocalDate.of(2000, 5, 5));

        assertThat(result.getName()).isEqualTo("Новое Имя");
        assertThat(result.getBirthdate()).isEqualTo(LocalDate.of(2000, 5, 5));
    }

    @Test
    void deposit_increasesBalance() {
        when(accountRepository.findByLogin("ivanov")).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any())).thenReturn(testAccount);

        Account result = accountService.deposit("ivanov", 50);

        assertThat(result.getSum()).isEqualTo(150);
    }

    @Test
    void withdraw_sufficientFunds_decreasesBalance() {
        when(accountRepository.findByLogin("ivanov")).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any())).thenReturn(testAccount);

        Account result = accountService.withdraw("ivanov", 30);

        assertThat(result.getSum()).isEqualTo(70);
    }

    @Test
    void withdraw_insufficientFunds_throwsException() {
        when(accountRepository.findByLogin("ivanov")).thenReturn(Optional.of(testAccount));

        assertThatThrownBy(() -> accountService.withdraw("ivanov", 200))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Недостаточно средств");
    }

    @Test
    void transfer_sufficientFunds_movesAmount() {
        Account petrov = new Account();
        petrov.setId(2L);
        petrov.setLogin("petrov");
        petrov.setName("Петров Петр");
        petrov.setBirthdate(LocalDate.of(1995, 5, 15));
        petrov.setSum(200);

        when(accountRepository.findByLogin("ivanov")).thenReturn(Optional.of(testAccount));
        when(accountRepository.findByLogin("petrov")).thenReturn(Optional.of(petrov));
        when(accountRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        accountService.transfer("ivanov", "petrov", 50);

        assertThat(testAccount.getSum()).isEqualTo(50);
        assertThat(petrov.getSum()).isEqualTo(250);
    }

    @Test
    void transfer_insufficientFunds_throwsException() {
        Account petrov = new Account();
        petrov.setLogin("petrov");
        petrov.setSum(200);

        when(accountRepository.findByLogin("ivanov")).thenReturn(Optional.of(testAccount));
        when(accountRepository.findByLogin("petrov")).thenReturn(Optional.of(petrov));

        assertThatThrownBy(() -> accountService.transfer("ivanov", "petrov", 500))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Недостаточно средств");
    }

    @Test
    void getOtherAccounts_excludesCurrentUser() {
        Account petrov = new Account();
        petrov.setLogin("petrov");
        petrov.setName("Петров Петр");

        when(accountRepository.findByLoginNot("ivanov")).thenReturn(List.of(petrov));

        List<Account> result = accountService.getOtherAccounts("ivanov");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getLogin()).isEqualTo("petrov");
    }
}
