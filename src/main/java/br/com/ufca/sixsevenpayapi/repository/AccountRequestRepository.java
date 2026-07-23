package br.com.ufca.sixsevenpayapi.repository;

import br.com.ufca.sixsevenpayapi.domain.entity.AccountRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRequestRepository extends JpaRepository<AccountRequest, Long> {
}
