package br.com.ufca.sixsevenpayapi.repository;

import br.com.ufca.sixsevenpayapi.domain.entity.Account;
import br.com.ufca.sixsevenpayapi.domain.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAccountNumber(String accountNumber);
    public boolean existsByAccountNumber(String AccountNumber);
    public boolean existsByCustomerIdAndAccountType(Long customerId, AccountType accountType);
    List<Account> findByBalanceLessThan(BigDecimal amount);
}
