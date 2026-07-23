package br.com.ufca.sixsevenpayapi.application.service;

import br.com.ufca.sixsevenpayapi.application.dto.PurchaseDTO;
import br.com.ufca.sixsevenpayapi.application.dto.PurchaseResponseDTO;
import br.com.ufca.sixsevenpayapi.domain.entity.CreditCard;
import br.com.ufca.sixsevenpayapi.domain.entity.Invoice;
import br.com.ufca.sixsevenpayapi.domain.entity.Purchase;
import br.com.ufca.sixsevenpayapi.domain.enums.InvoiceStatus;
import br.com.ufca.sixsevenpayapi.repository.CreditCardRepository;
import br.com.ufca.sixsevenpayapi.repository.InvoiceRepository;
import br.com.ufca.sixsevenpayapi.repository.PurchaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CreditCardService {

    private final CreditCardRepository creditCardRepository;
    private final InvoiceRepository invoiceRepository;
    private final PurchaseRepository purchaseRepository;
    public CreditCardService(CreditCardRepository creditCardRepository, InvoiceRepository invoiceRepository, PurchaseRepository purchaseRepository) {
        this.creditCardRepository = creditCardRepository;
        this.invoiceRepository = invoiceRepository;
        this.purchaseRepository = purchaseRepository;

    }


    @Transactional
    public PurchaseResponseDTO processPurchase(PurchaseDTO dto){
        CreditCard creditCard = creditCardRepository.findByCardNumber(dto.cardNumber())
                .orElseThrow(() -> new RuntimeException("Cartão de crédito não encontrado"));

        if(!creditCard.getCustomer().isActive()){
            throw new RuntimeException("Cliente inativo");
        }

        if(!dto.cvv().equals(creditCard.getCvv())){
            throw new RuntimeException("CVV inválido");
        }

        BigDecimal avaibleLimit = creditCard.getCreditLimit().subtract(creditCard.getCurrentSpending());
        if(dto.amount().compareTo(avaibleLimit) > 0){
            throw new RuntimeException("Limite insuficiente para compra");
        }

        Optional<Invoice> existingInvoice = invoiceRepository.findByCreditCardIdAndStatus(creditCard.getId(), InvoiceStatus.PENDING);
        Invoice openInvoice;
        if(existingInvoice.isPresent()){
            openInvoice = existingInvoice.get();
        }else {
            Invoice newInvoice = new Invoice(InvoiceStatus.PENDING, creditCard);
            openInvoice = invoiceRepository.save(newInvoice);
        }

        creditCard.setCurrentSpending(creditCard.getCurrentSpending().add(dto.amount()));
        creditCardRepository.save(creditCard);

        Purchase purchase = new Purchase(openInvoice, dto.amount(), dto.description());
        purchaseRepository.save(purchase);

        openInvoice.setTotalAmount(openInvoice.getTotalAmount().add(dto.amount()));
        invoiceRepository.save(openInvoice);
        return PurchaseResponseDTO.fromEntity(purchase);

    }

}
