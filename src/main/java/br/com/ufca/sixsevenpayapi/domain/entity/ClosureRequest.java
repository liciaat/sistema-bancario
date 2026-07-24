package br.com.ufca.sixsevenpayapi.domain.entity;

import br.com.ufca.sixsevenpayapi.domain.enums.RequestType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "closure_requests")
public class ClosureRequest extends Request {

    protected ClosureRequest() {
        super();
    }

    public ClosureRequest(Customer customer) {
        super(customer, RequestType.CLOSURE);
    }
}
