package ru.yandex.practicum.mybankfront.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.client.RestClientResponseException;
import ru.yandex.practicum.mybankfront.client.AccountsClient;
import ru.yandex.practicum.mybankfront.client.CashClient;
import ru.yandex.practicum.mybankfront.client.TransferClient;
import ru.yandex.practicum.mybankfront.controller.dto.AccountDto;
import ru.yandex.practicum.mybankfront.controller.dto.CashAction;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AccountFacadeService {

    private final SecurityService securityService;
    private final AccountsClient accountsClient;
    private final CashClient cashClient;
    private final TransferClient transferClient;

    public void loadAccount(Model model) {
        String login = securityService.getCurrentUserLogin();
        String token = securityService.getAccessToken();
        try {
            Map<String, Object> account = accountsClient.getAccount(login, token);
            List<AccountDto> accounts = accountsClient.getOtherAccounts(login, token);
            fillModel(model, account, accounts, null, null);
        } catch (RestClientResponseException e) {
            fillErrorModel(model, e.getResponseBodyAsString());
        }
    }

    public void updateAccount(Model model, String name, LocalDate birthdate) {
        String login = securityService.getCurrentUserLogin();
        String token = securityService.getAccessToken();
        try {
            Map<String, Object> account = accountsClient.updateAccount(login, name, birthdate, token);
            List<AccountDto> accounts = accountsClient.getOtherAccounts(login, token);
            fillModel(model, account, accounts, null, "Данные обновлены");
        } catch (RestClientResponseException e) {
            reloadWithError(model, e.getResponseBodyAsString());
        }
    }

    public void processCash(Model model, int value, CashAction action) {
        String login = securityService.getCurrentUserLogin();
        String token = securityService.getAccessToken();
        try {
            Map<String, Object> account = cashClient.processCash(login, value, action.name(), token);
            List<AccountDto> accounts = accountsClient.getOtherAccounts(login, token);
            String info = action == CashAction.GET
                    ? "Снято %d руб".formatted(value)
                    : "Положено %d руб".formatted(value);
            fillModel(model, account, accounts, null, info);
        } catch (RestClientResponseException e) {
            reloadWithError(model, e.getResponseBodyAsString());
        }
    }

    public void transfer(Model model, int value, String toLogin) {
        String login = securityService.getCurrentUserLogin();
        String token = securityService.getAccessToken();
        try {
            Map<String, Object> account = transferClient.transfer(login, toLogin, value, token);
            List<AccountDto> accounts = accountsClient.getOtherAccounts(login, token);
            fillModel(model, account, accounts, null, "Успешно переведено %d руб".formatted(value));
        } catch (RestClientResponseException e) {
            reloadWithError(model, e.getResponseBodyAsString());
        }
    }

    private void reloadWithError(Model model, String error) {
        String login = securityService.getCurrentUserLogin();
        String token = securityService.getAccessToken();
        try {
            Map<String, Object> account = accountsClient.getAccount(login, token);
            List<AccountDto> accounts = accountsClient.getOtherAccounts(login, token);
            fillModel(model, account, accounts, List.of(error), null);
        } catch (RestClientResponseException e) {
            fillErrorModel(model, error);
        }
    }

    private void fillModel(Model model, Map<String, Object> account, List<AccountDto> accounts,
                           List<String> errors, String info) {
        model.addAttribute("name", account.get("name"));
        model.addAttribute("birthdate", account.get("birthdate"));
        model.addAttribute("sum", account.get("sum"));
        model.addAttribute("accounts", accounts);
        model.addAttribute("errors", errors);
        model.addAttribute("info", info);
    }

    private void fillErrorModel(Model model, String error) {
        model.addAttribute("name", "");
        model.addAttribute("birthdate", "");
        model.addAttribute("sum", 0);
        model.addAttribute("accounts", List.of());
        model.addAttribute("errors", List.of(error));
        model.addAttribute("info", null);
    }
}
