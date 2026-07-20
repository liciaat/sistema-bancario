package br.com.ufca.sixsevenpayapi.domain.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "credit_cards")
public class CreditCard extends BaseEntity {

    @Column(name = "card_number", nullable = false, unique = true, length = 16)
    private String cardNumber;

    @Column(name = "credit_limit", nullable = false, precision = 19, scale = 2)
    private BigDecimal creditLimit;

    @Column(name = "current_spending", nullable = false, precision = 19, scale = 2)
    private BigDecimal currentSpending;

    @OneToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @OneToMany(mappedBy = "creditCard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invoice> invoices = new ArrayList<>();

    protected  CreditCard() {
        super();
    }

    public CreditCard(BigDecimal creditLimit, String cardNumber, Customer customer) {
        this.creditLimit = creditLimit;
        this.cardNumber = cardNumber;
        this.customer = customer;
        this.currentSpending = BigDecimal.ZERO;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getCurrentSpending() {
        return currentSpending;
    }

    public void setCurrentSpending(BigDecimal currentSpending) {
        this.currentSpending = currentSpending;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }
}
