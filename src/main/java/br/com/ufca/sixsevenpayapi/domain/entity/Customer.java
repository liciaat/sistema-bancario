package br.com.ufca.sixsevenpayapi.domain.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer extends User {

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Account> accounts = new ArrayList<>();

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private CreditCard creditCard;

    @Column(name = "transaction_password", nullable = false)
    private String transactionPassword;

    public Customer() {
    }

    public Customer(String fullName,
                    String cpf,
                    String email,
                    String password,
                    String phone,
                    String transactionPassword) {
        super(fullName, cpf, email, password,phone);
        this.transactionPassword = transactionPassword;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    public String getTransactionPassword() {
        return transactionPassword;
    }

    public void setTranscationPassword(String transactionPassword) {
        this.transactionPassword = transactionPassword;
    }
}
