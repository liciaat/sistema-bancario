package br.com.ufca.sixsevenpayapi.repository;

import br.com.ufca.sixsevenpayapi.domain.entity.Account;
import br.com.ufca.sixsevenpayapi.domain.enums.AccountStatus;
import br.com.ufca.sixsevenpayapi.domain.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String AccountNumber);
    boolean existsByCustomerIdAndAccountType(Long customerId, AccountType accountType);
    List<Account> findByBalanceLessThan(BigDecimal amount);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(a.balance) FROM Account a")
    BigDecimal getTotalBankBalance();
    long countByAccountStatus(AccountStatus accountStatus);
    List<Account> findByAccountTypeAndAccountStatus(AccountType accountType, AccountStatus accountStatus);
}
