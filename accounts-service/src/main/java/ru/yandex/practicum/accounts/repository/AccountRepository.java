package ru.yandex.practicum.accounts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.accounts.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByLogin(String login);

    List<Account> findByLoginNot(String login);
}
