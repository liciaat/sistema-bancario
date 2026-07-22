package br.com.ufca.sixsevenpayapi.domain.entity;

import br.com.ufca.sixsevenpayapi.domain.enums.RequestStatus;
import br.com.ufca.sixsevenpayapi.domain.enums.RequestType;
import jakarta.persistence.*;

@Entity
@Table(name = "requests")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Request extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name ="request_type", nullable = false)
    private RequestType type;

    @ManyToOne
    @JoinColumn(name = "custome_id", nullable = false)
    private Customer customer;

    @Column(name = "reject_reason")
    private String rejectReason;

    protected Request() {
        this.status = RequestStatus.PENDING;
    }

    public Request(Customer customer, RequestType type) {
        this();
        this.type = type;
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

    public RequestType getType() {
        return type;
    }

    public void setType(RequestType type) {
        this.type = type;
    }
}
