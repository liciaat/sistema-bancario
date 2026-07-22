package br.com.ufca.sixsevenpayapi.domain.entity;

import br.com.ufca.sixsevenpayapi.domain.enums.RequestType;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "credit_requests")
public class CreditRequest extends Request {
    @Column(name = "requested_limit", nullable = false, precision = 19, scale = 2)
    private BigDecimal requestedLimit;

    @Enumerated(EnumType.STRING)
    @Column(name ="request_type", nullable = false)
    private RequestType requestType;


    protected CreditRequest() {
        super();
    }

    public CreditRequest(Customer customer,BigDecimal requestedLimit) {
        super(customer);
        this.requestedLimit = requestedLimit;
        this.requestType = RequestType.CREDIT;
    }


    public BigDecimal getRequestedLimit() {
        return requestedLimit;
    }

    public void setRequestedLimit(BigDecimal requestedLimit) {
        this.requestedLimit = requestedLimit;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }
}
