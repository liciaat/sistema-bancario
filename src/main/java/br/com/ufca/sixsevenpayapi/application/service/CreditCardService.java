package br.com.ufca.sixsevenpayapi.application.service;

import br.com.ufca.sixsevenpayapi.application.dto.*;
import br.com.ufca.sixsevenpayapi.domain.entity.*;
import br.com.ufca.sixsevenpayapi.domain.enums.AccountStatus;
import br.com.ufca.sixsevenpayapi.domain.enums.InvoiceStatus;
import br.com.ufca.sixsevenpayapi.domain.enums.TransactionType;
import br.com.ufca.sixsevenpayapi.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CreditCardService {

    private final CreditCardRepository creditCardRepository;
    private final InvoiceRepository invoiceRepository;
    private final PurchaseRepository purchaseRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public CreditCardService(CreditCardRepository creditCardRepository,
                             InvoiceRepository invoiceRepository,
                             PurchaseRepository purchaseRepository,
                             AccountRepository accountRepository,
                             TransactionRepository transactionRepository) {
        this.creditCardRepository = creditCardRepository;
        this.invoiceRepository = invoiceRepository;
        this.purchaseRepository = purchaseRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }


    @Transactional
    public PurchaseResponseDTO processPurchase(PurchaseDTO dto){
        CreditCard creditCard = creditCardRepository.findByCardNumber(dto.cardNumber())
                .orElseThrow(() -> new RuntimeException("Cartão de crédito não encontrado"));

        if(!creditCard.getCustomer().isActive()){
            throw new RuntimeException("Cliente inativo");
        }

        if(!dto.transactionPassword().equals(creditCard.getCustomer().getTranscationPassword())){
            throw  new RuntimeException("Senha de transação incorreta");
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

    @Transactional(readOnly = true)
    public InvoiceResponseDTO getInvoice(InvoiceDTO dto){
        Invoice invoice = invoiceRepository.findById(dto.invoiceId())
                .orElseThrow(() -> new RuntimeException("Fatura não encontrada"));

        return InvoiceResponseDTO.fromEntity(invoice);
    }

    @Transactional
    public InvoiceResponseDTO payInvoice(PayInvoiceDTO dto){
        Invoice invoice = invoiceRepository.findById(dto.invoiceId())
                .orElseThrow(() -> new RuntimeException("Fatura não encontrada"));
        if(invoice.getStatus().equals(InvoiceStatus.PAID)){
            throw new RuntimeException("Esta fatura já está paga");
        }

        Account account = accountRepository.findByAccountNumber(dto.accountNumber())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        if(!account.getCustomer().equals(invoice.getCreditCard().getCustomer())){
            throw new RuntimeException("A conta informada não pertence ao titular do cartão");
        }

        if(account.getAccountStatus() != AccountStatus.ACTIVE){
            throw new RuntimeException("A conta está bloqueada");
        }

        if(!dto.transactionPassword().equals(account.getCustomer().getTranscationPassword())){
            throw new RuntimeException("Senha de transação incorreta");
        }

        if(account.getBalance().compareTo(invoice.getTotalAmount()) < 0){
            throw new RuntimeException("Saldo insuficiente para pagar a fatura");
        }

        account.setBalance(account.getBalance().subtract(invoice.getTotalAmount()));
        accountRepository.save(account);

        Transaction transaction = new Transaction(account, invoice.getTotalAmount().negate(), TransactionType.INVOICE);
        transactionRepository.save(transaction);

        invoice.setStatus(InvoiceStatus.PAID);
        invoiceRepository.save(invoice);

        CreditCard creditCard = invoice.getCreditCard();
        creditCard.setCurrentSpending(creditCard.getCurrentSpending().subtract(invoice.getTotalAmount()));
        creditCardRepository.save(creditCard);

        return InvoiceResponseDTO.fromEntity(invoice);
    }

    @Transactional(readOnly = true)
    public CreditCardResponseDTO getCreditCardByCustomer(Long customerId){
        CreditCard creditCard = creditCardRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Cliente não possui cartão de crédito"));
        return CreditCardResponseDTO.fromEntity(creditCard);
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponseDTO> getCustomerInvoices(Long customerId){
        CreditCard creditCard = creditCardRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Cliente não possui cartão de crédito"));

        List<Invoice> invoices = creditCard.getInvoices();
        List<InvoiceResponseDTO> dtos = new ArrayList<>();
        for(Invoice invoice : invoices){
            dtos.add(InvoiceResponseDTO.fromEntity(invoice));
        }
        return dtos;

    }

}
