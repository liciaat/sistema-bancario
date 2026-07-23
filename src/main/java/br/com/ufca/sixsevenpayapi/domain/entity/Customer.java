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

    @Column(name = "transcation_password", nullable = false)
    private String transcationPassword;

    public Customer() {
    }

    public Customer(String fullName,
                    String cpf,
                    String email,
                    String password,
                    String phone,
                    String transcationPassword) {
        super(fullName, cpf, email, password,phone);
        this.transcationPassword = transcationPassword;
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

    public String getTranscationPassword() {
        return transcationPassword;
    }

    public void setTranscationPassword(String transcationPassword) {
        this.transcationPassword = transcationPassword;
    }
}
