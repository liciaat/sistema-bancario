package br.com.ufca.sixsevenpayapi.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_credit_requests")
public class CreditRequest extends Request {
    @Column(name = "requested_limit", nullable = false, precision = 19, scale = 2)
    private BigDecimal requestedLimit;

    protected CreditRequest() {
        super();
    }

    public CreditRequest(BigDecimal requestedLimit) {
        super();
        this.requestedLimit = requestedLimit;
    }


    public BigDecimal getRequestedLimit() {
        return requestedLimit;
    }

    public void setRequestedLimit(BigDecimal requestedLimit) {
        this.requestedLimit = requestedLimit;
    }
}
