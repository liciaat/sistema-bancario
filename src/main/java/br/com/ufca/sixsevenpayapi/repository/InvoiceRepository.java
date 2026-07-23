package br.com.ufca.sixsevenpayapi.repository;

import br.com.ufca.sixsevenpayapi.domain.entity.Invoice;
import br.com.ufca.sixsevenpayapi.domain.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByCreditCardIdAndStatus(Long creditCardId, InvoiceStatus status);
}
