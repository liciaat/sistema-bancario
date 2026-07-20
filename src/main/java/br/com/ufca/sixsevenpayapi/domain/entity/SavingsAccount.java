package br.com.ufca.sixsevenpayapi.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "savings_accounts")
public class SavingsAccount extends Account {

    protected SavingsAccount() {
    }

    public SavingsAccount(Customer customer, String accountNumber) {
        super(customer, accountNumber);
    }
}
