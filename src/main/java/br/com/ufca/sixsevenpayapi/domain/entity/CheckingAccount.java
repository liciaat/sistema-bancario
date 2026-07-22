package br.com.ufca.sixsevenpayapi.domain.entity;

import br.com.ufca.sixsevenpayapi.domain.enums.AccountType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "checking_accounts")
public class CheckingAccount extends Account {

    protected CheckingAccount() {
        super();
        super.setAccountType(AccountType.CHECKING);
    }

    public CheckingAccount(Customer customer, String accountNumber) {
        super(customer, accountNumber);
        super.setAccountType(AccountType.CHECKING);
    }

}
