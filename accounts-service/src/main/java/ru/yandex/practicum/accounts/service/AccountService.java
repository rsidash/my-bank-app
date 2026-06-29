package ru.yandex.practicum.accounts.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.accounts.model.Account;
import ru.yandex.practicum.accounts.repository.AccountRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final Counter depositsCounter;
    private final Counter withdrawalsCounter;
    private final Counter transfersCounter;
    private final Counter accountUpdatesCounter;

    public AccountService(AccountRepository accountRepository, MeterRegistry registry) {
        this.accountRepository = accountRepository;
        this.depositsCounter = Counter.builder("accounts.deposits.total")
                .description("Total number of deposit operations").register(registry);
        this.withdrawalsCounter = Counter.builder("accounts.withdrawals.total")
                .description("Total number of withdrawal operations").register(registry);
        this.transfersCounter = Counter.builder("accounts.transfers.total")
                .description("Total number of transfer operations").register(registry);
        this.accountUpdatesCounter = Counter.builder("accounts.updates.total")
                .description("Total number of account profile updates").register(registry);
    }

    public Account getAccount(String login) {
        return accountRepository.findByLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("Аккаунт не найден: " + login));
    }

    @Transactional
    public Account updateAccount(String login, String name, LocalDate birthdate) {
        Account account = getAccount(login);
        account.setName(name);
        account.setBirthdate(birthdate);
        accountUpdatesCounter.increment();
        return accountRepository.save(account);
    }

    public List<Account> getOtherAccounts(String login) {
        return accountRepository.findByLoginNot(login);
    }

    @Transactional
    public Account deposit(String login, int amount) {
        Account account = getAccount(login);
        account.setSum(account.getSum() + amount);
        depositsCounter.increment();
        return accountRepository.save(account);
    }

    @Transactional
    public Account withdraw(String login, int amount) {
        Account account = getAccount(login);
        if (account.getSum() < amount) {
            throw new IllegalStateException("Недостаточно средств на счету");
        }
        account.setSum(account.getSum() - amount);
        withdrawalsCounter.increment();
        return accountRepository.save(account);
    }

    @Transactional
    public void transfer(String fromLogin, String toLogin, int amount) {
        Account from = getAccount(fromLogin);
        Account to = getAccount(toLogin);
        if (from.getSum() < amount) {
            throw new IllegalStateException("Недостаточно средств на счету");
        }
        from.setSum(from.getSum() - amount);
        to.setSum(to.getSum() + amount);
        accountRepository.save(from);
        accountRepository.save(to);
        transfersCounter.increment();
    }
}
