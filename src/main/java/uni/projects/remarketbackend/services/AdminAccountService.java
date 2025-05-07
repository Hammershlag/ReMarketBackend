package uni.projects.remarketbackend.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uni.projects.remarketbackend.dao.AccountRepository;
import uni.projects.remarketbackend.dto.AccountDto;
import uni.projects.remarketbackend.dto.admin.ExtendedAccountDto;
import uni.projects.remarketbackend.exceptions.exceptions.NotFoundException;
import uni.projects.remarketbackend.models.account.Account;
import uni.projects.remarketbackend.models.account.Status;

import java.time.LocalDateTime;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@Service
public class AdminAccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Page<ExtendedAccountDto> getAllAccounts(Pageable pageable) {
        return accountRepository.findAll(pageable)
                .map(ExtendedAccountDto::fromAccount);
    }

    @SneakyThrows
    public void blockAccount(Long id, HttpServletRequest request) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        if (account.getStatus() == Status.DISABLED) {
            account.setStatus(Status.ACTIVE);
            account.setDisabledAt(null);
        } else if (account.getStatus() == Status.ACTIVE) {
            account.setStatus(Status.DISABLED);
            account.setDisabledAt(LocalDateTime.now());
        } else {
            throw new NotFoundException("Account not found");
        }
        accountRepository.save(account);
    }
}
