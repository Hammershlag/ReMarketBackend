package uni.projects.remarketbackend.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uni.projects.remarketbackend.dto.AccountDto;
import uni.projects.remarketbackend.services.AdminAccountService;

import java.util.List;

/**
 * @author Tomasz Zbroszczyk
 * @version 1.0
 * @since 27.04.2025
 */

@RestController
@RequestMapping("/api/admin/accounts")
public class AdminAccountController {

    @Autowired
    private AdminAccountService adminUserService;

    @GetMapping
    public ResponseEntity<Page<AccountDto>> getAllAccounts(Pageable pageable) {
        Page<AccountDto> accounts = adminUserService.getAllAccounts(pageable);
        return ResponseEntity.ok(accounts);
    }

}
