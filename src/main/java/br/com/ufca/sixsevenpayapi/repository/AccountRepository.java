package br.com.ufca.sixsevenpayapi.repository;

import br.com.ufca.sixsevenpayapi.domain.entity.Account;
import br.com.ufca.sixsevenpayapi.domain.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    public boolean existsByAccountNumber(String AccountNumber);
    public boolean existsByCustomerIdAndAccountType(Long customerId, AccountType accountType);
}
