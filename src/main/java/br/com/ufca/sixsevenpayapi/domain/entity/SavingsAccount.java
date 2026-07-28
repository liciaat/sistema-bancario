package br.com.ufca.sixsevenpayapi.domain.entity;

import br.com.ufca.sixsevenpayapi.domain.enums.AccountType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "savings_accounts")
public class SavingsAccount extends Account {

    protected SavingsAccount() {
        super.setAccountType(AccountType.SAVINGS);
    }

    public SavingsAccount(Customer customer, String accountNumber) {
        super(customer, accountNumber);
        super.setAccountType(AccountType.SAVINGS);
    }

    public BigDecimal calculateMonthlyYield(BigDecimal rateMultiplier) {
        return getBalance().multiply(rateMultiplier).setScale(2, RoundingMode.HALF_UP);
    }

    public void applyMonthlyYield(BigDecimal rateMultiplier) {
        deposit(calculateMonthlyYield(rateMultiplier));
    }
}
