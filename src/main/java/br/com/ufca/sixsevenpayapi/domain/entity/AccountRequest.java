package br.com.ufca.sixsevenpayapi.domain.entity;

import br.com.ufca.sixsevenpayapi.domain.enums.AccountType;
import br.com.ufca.sixsevenpayapi.domain.enums.RequestType;
import jakarta.persistence.*;

@Entity
@Table(name = "account_requests")
public class AccountRequest extends Request {
    @Enumerated(EnumType.STRING)
    @Column(name = "requested_account_type", nullable = false)
    private AccountType requestedAccountType;

    @Enumerated(EnumType.STRING)
    @Column(name ="request_type", nullable = false)
    private RequestType requestType;

    protected AccountRequest() {
        super();
    }

    public AccountRequest(Customer customer, AccountType requestedAccountType) {
        super(customer);
        this.requestType = RequestType.ACCOUNT;
        this.requestedAccountType = requestedAccountType;
    }

    public AccountType getRequestedAccountType() {
        return requestedAccountType;
    }

    public void setRequestedAccountType(AccountType requestedAccountType) {
        this.requestedAccountType = requestedAccountType;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }
}
