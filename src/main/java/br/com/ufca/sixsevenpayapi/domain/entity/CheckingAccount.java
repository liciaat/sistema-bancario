package br.com.ufca.sixsevenpayapi.domain.entity;

import br.com.ufca.sixsevenpayapi.domain.enums.AccountType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "checking_accounts")
public class CheckingAccount extends Account {

    private static final BigDecimal DEFAULT_OVERDRAFT_LIMIT = new BigDecimal("500.00");

    private BigDecimal overdraftLimit;

    protected CheckingAccount() {
        super();
        super.setAccountType(AccountType.CHECKING);
        this.overdraftLimit = DEFAULT_OVERDRAFT_LIMIT;
    }

    public CheckingAccount(Customer customer, String accountNumber) {
        super(customer, accountNumber);
        super.setAccountType(AccountType.CHECKING);
        this.overdraftLimit = DEFAULT_OVERDRAFT_LIMIT;
    }

    public BigDecimal getOverdraftLimit() {
        return overdraftLimit;
    }

    public BigDecimal getAvailableBalance() {
        BigDecimal positiveBalance = getBalance().max(BigDecimal.ZERO);
        return positiveBalance.add(overdraftLimit);
    }

    @Override
    public boolean canDebit(BigDecimal amount) {
        return getAvailableBalance().compareTo(amount) >= 0;
    }

    @Override
    public void debit(BigDecimal amount) {
        if (!canDebit(amount)) {
            throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException(
                    "Saldo insuficiente. O valor ultrapassa o limite da conta.");
        }

        BigDecimal amountCoveredByOverdraft = amount.subtract(getBalance().max(BigDecimal.ZERO));
        if (amountCoveredByOverdraft.compareTo(BigDecimal.ZERO) > 0) {
            overdraftLimit = overdraftLimit.subtract(amountCoveredByOverdraft);
        }
        setBalance(getBalance().subtract(amount));
    }
}
