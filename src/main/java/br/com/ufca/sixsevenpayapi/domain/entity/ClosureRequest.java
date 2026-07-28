package br.com.ufca.sixsevenpayapi.domain.entity;

import br.com.ufca.sixsevenpayapi.common.exception.BadRequestException;
import br.com.ufca.sixsevenpayapi.domain.enums.RequestType;
import br.com.ufca.sixsevenpayapi.domain.enums.InvoiceStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "closure_requests")
public class ClosureRequest extends Request {

    protected ClosureRequest() {
        super();
    }

    public ClosureRequest(Customer customer) {
        super(customer, RequestType.CLOSURE);
    }

    public void validateClosingEligibility() {
        Customer customer = getCustomer();

        for (Account account : customer.getAccounts()) {
            if (account.getBalance() != null && account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
                throw new BadRequestException("Não é possível encerrar o perfil: a conta "
                        + account.getAccountNumber() + " possui saldo pendente");
            }
        }

        CreditCard creditCard = customer.getCreditCard();
        if (creditCard != null && creditCard.getCurrentSpending().compareTo(BigDecimal.ZERO) > 0) {
            throw new BadRequestException("Não é possível encerrar o perfil: ainda existe dívida no cartão");
        }

        if (creditCard != null && creditCard.getInvoices().stream()
                .anyMatch(invoice -> invoice.getStatus() == InvoiceStatus.PENDING)) {
            throw new BadRequestException("Não é possível encerrar o perfil: existe fatura pendente");
        }
    }
}
