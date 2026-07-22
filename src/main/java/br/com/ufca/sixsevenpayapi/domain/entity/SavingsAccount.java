package br.com.ufca.sixsevenpayapi.domain.entity;

import br.com.ufca.sixsevenpayapi.domain.enums.AccountType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

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
}
