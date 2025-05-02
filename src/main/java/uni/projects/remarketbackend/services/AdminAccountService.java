package uni.projects.remarketbackend.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uni.projects.remarketbackend.dao.AccountRepository;
import uni.projects.remarketbackend.dto.AccountDto;
import uni.projects.remarketbackend.exceptions.exceptions.NotFoundException;
import uni.projects.remarketbackend.models.account.Account;
import uni.projects.remarketbackend.models.account.Status;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@Service
public class AdminAccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Page<AccountDto> getAllAccounts(Pageable pageable) {
        return accountRepository.findAll(pageable)
                .map(AccountDto::fromAccount);
    }

    @SneakyThrows
    public void blockAccount(Long id, HttpServletRequest request) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        account.setStatus(Status.DISABLED);
        accountRepository.save(account);
    }
}
