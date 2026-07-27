package br.com.ufca.sixsevenpayapi.repository;

import br.com.ufca.sixsevenpayapi.domain.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ManagerRepository extends JpaRepository<Manager, Long> {
    List<Manager> findByActiveTrue();
    List<Manager> findByActiveFalse();
    Optional<Manager> findByCpf(String cpf);
}
