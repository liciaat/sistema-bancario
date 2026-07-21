package br.com.ufca.sixsevenpayapi.domain.entity;

import br.com.ufca.sixsevenpayapi.domain.enums.RequestStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "requests")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Request extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status;

    @ManyToOne
    @JoinColumn(name = "custome_id", nullable = false)
    private Customer customer;

    @Column(name = "reject_reason")
    private String rejectReason;

    protected Request() {
        this.status = RequestStatus.PENDING;
    }

    public Request(Customer customer) {
        this();
        this.customer = customer;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
}
