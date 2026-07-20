package br.com.ufca.sixsevenpayapi.domain.entity;

import br.com.ufca.sixsevenpayapi.domain.enums.AccountType;
import jakarta.persistence.*;

@Entity
@Table(name = "account_requests")
public class AccountRequest extends Request {
    @Enumerated(EnumType.STRING)
    @Column(name = "requested_account_type", nullable = false)
    private AccountType requestedAccountType;

    protected AccountRequest() {
        super();
    }

    public AccountRequest(AccountType requestedAccountType) {
        super();
        this.requestedAccountType = requestedAccountType;
    }

    public AccountType getRequestedAccountType() {
        return requestedAccountType;
    }

    public void setRequestedAccountType(AccountType requestedAccountType) {
        this.requestedAccountType = requestedAccountType;
    }
}
