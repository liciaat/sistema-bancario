package br.com.ufca.sixsevenpayapi.repository;

import br.com.ufca.sixsevenpayapi.domain.entity.Request;
import br.com.ufca.sixsevenpayapi.domain.enums.RequestStatus;
import br.com.ufca.sixsevenpayapi.domain.enums.RequestType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    public List<Request> findByCustomerId(Long customerId);
    public boolean existsByCustomerIdAndTypeAndStatus(Long customerId, RequestType requestType, RequestStatus requestStatus);
    public List<Request> findByStatus(RequestStatus status);
    Long countByStatus(RequestStatus status);

}
