package br.com.ufca.sixsevenpayapi.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_checking_accounts")
public class CheckingAccount extends Account {

    @Column(name = "overdraft_limit", nullable = false, precision = 19, scale = 2)
    private BigDecimal overdraftLimit;

    protected CheckingAccount() {
        super();
    }

    public CheckingAccount(Customer customer, String accountNumber) {
        super(customer, accountNumber);
        this.overdraftLimit = BigDecimal.ZERO;
    }

    public BigDecimal getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(BigDecimal overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }
}
