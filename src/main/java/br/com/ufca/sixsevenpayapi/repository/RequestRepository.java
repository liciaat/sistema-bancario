package br.com.ufca.sixsevenpayapi.repository;

import br.com.ufca.sixsevenpayapi.domain.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByCustomerId(Long customerId);
}
