package br.com.ufca.sixsevenpayapi.application.service;

import br.com.ufca.sixsevenpayapi.application.dto.*;
import br.com.ufca.sixsevenpayapi.domain.entity.*;
import br.com.ufca.sixsevenpayapi.domain.enums.AccountStatus;
import br.com.ufca.sixsevenpayapi.domain.enums.AccountType;
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
                        .orElseThrow(() -> new br.com.ufca.sixsevenpayapi.common.exception.NotFoundException("Cartão de crédito não encontrado"));

        if(!creditCard.getCustomer().isActive()){
                    throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException("Cliente inativo");
        }

        if(!dto.transactionPassword().equals(creditCard.getCustomer().getTransactionPassword())){
                    throw  new br.com.ufca.sixsevenpayapi.common.exception.UnauthorizedException("Senha de transação incorreta");
        }

        if(!dto.cvv().equals(creditCard.getCvv())){
                    throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException("CVV inválido");
        }

        BigDecimal avaibleLimit = creditCard.getCreditLimit().subtract(creditCard.getCurrentSpending());
        if(dto.amount().compareTo(avaibleLimit) > 0){
                    throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException("Limite insuficiente para compra");
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
    public InvoiceResponseDTO getInvoice(Long invoiceId){
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new br.com.ufca.sixsevenpayapi.common.exception.NotFoundException("Fatura não encontrada"));

        return InvoiceResponseDTO.fromEntity(invoice);
    }

    @Transactional
    public InvoiceResponseDTO payInvoice(Long invoiceId, PayInvoiceDTO dto){
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new br.com.ufca.sixsevenpayapi.common.exception.NotFoundException("Fatura não encontrada"));
        if(invoice.getStatus().equals(InvoiceStatus.PAID)){
            throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException("Esta fatura já está paga");
        }

        Account account = accountRepository.findByAccountNumber(dto.accountNumber())
                .orElseThrow(() -> new br.com.ufca.sixsevenpayapi.common.exception.NotFoundException("Conta não encontrada"));

        if(!account.getCustomer().equals(invoice.getCreditCard().getCustomer())){
            throw new br.com.ufca.sixsevenpayapi.common.exception.ForbiddenException("A conta informada não pertence ao titular do cartão");
        }

        if(account.getAccountStatus() != AccountStatus.ACTIVE){
            throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException("A conta está bloqueada");
        }

        if(!dto.transactionPassword().equals(account.getCustomer().getTransactionPassword())){
            throw new br.com.ufca.sixsevenpayapi.common.exception.UnauthorizedException("Senha de transação incorreta");
        }

        BigDecimal availableLimit = account.getBalance();
        if (account.getAccountType() == AccountType.CHECKING) {
            availableLimit = availableLimit.add(new BigDecimal("500.00")); // Limite do cheque especial
        }

        if(availableLimit.compareTo(invoice.getTotalAmount()) < 0){
            throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException("Saldo insuficiente para pagar a fatura");
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
                .orElseThrow(() -> new br.com.ufca.sixsevenpayapi.common.exception.NotFoundException("Cliente não possui cartão de crédito"));
        return CreditCardResponseDTO.fromEntity(creditCard);
    }

    @Transactional(readOnly = true)
    public List<InvoiceResponseDTO> getCustomerInvoices(Long customerId){
        CreditCard creditCard = creditCardRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new br.com.ufca.sixsevenpayapi.common.exception.NotFoundException("Cliente não possui cartão de crédito"));

        List<Invoice> invoices = creditCard.getInvoices();
        List<InvoiceResponseDTO> dtos = new ArrayList<>();
        for(Invoice invoice : invoices){
            dtos.add(InvoiceResponseDTO.fromEntity(invoice));
        }
        return dtos;

    }

}
