package br.com.ufca.sixsevenpayapi.application.service;

import br.com.ufca.sixsevenpayapi.application.dto.CreditRequestDTO;
import br.com.ufca.sixsevenpayapi.application.dto.RequestResponseDTO;
import br.com.ufca.sixsevenpayapi.application.dto.SavingsAccountRequestDTO;
import br.com.ufca.sixsevenpayapi.domain.entity.*;
import br.com.ufca.sixsevenpayapi.domain.enums.AccountType;
import br.com.ufca.sixsevenpayapi.domain.enums.RequestStatus;
import br.com.ufca.sixsevenpayapi.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static br.com.ufca.sixsevenpayapi.domain.utils.GenerateNumber.*;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final ManagerRepository managerRepository;
    private final CreditCardRepository creditCardRepository;

    public RequestService(RequestRepository requestRepository,
                          AccountRepository accountRepository,
                          CustomerRepository customerRepository,
                          ManagerRepository managerRepository,
                          CreditCardRepository creditCardRepository) {
        this.requestRepository = requestRepository;
        this.accountRepository =  accountRepository;
        this.customerRepository = customerRepository;
        this.managerRepository = managerRepository;
        this.creditCardRepository = creditCardRepository;
    }

    @Transactional
    public RequestResponseDTO requestSavingsAccount(SavingsAccountRequestDTO dto){

        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new RuntimeException("Cliente com esse Id não existe"));

        AccountRequest request = new AccountRequest(customer, AccountType.SAVINGS);
        AccountRequest savedRequest = requestRepository.save(request);
        return RequestResponseDTO.fromEntity(savedRequest);

    }

    @Transactional
    public RequestResponseDTO requestCredit(CreditRequestDTO dto){

        Customer customer = customerRepository.findById(dto.customerId())
                .orElseThrow(() -> new RuntimeException("Cliente com esse Id não existe"));

        CreditRequest request = new CreditRequest(customer, dto.requestedLimit());
        CreditRequest savedRequest = requestRepository.save(request);
        return RequestResponseDTO.fromEntity(savedRequest);
    }

    @Transactional
    public RequestResponseDTO approveRequest(Long requestId, Long managerId){

        if(!managerRepository.existsById(managerId)){
            throw new RuntimeException("Id de Gerente inválido");
        }
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Id da solicitação inválido"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Apenas solicitações com status PENDENTE podem ser aprovadas");
        }
        if(request instanceof AccountRequest){
            String accountNumber = generateAccountNumber();
            while (accountRepository.existsByAccountNumber(accountNumber)){
                accountNumber = generateAccountNumber();
            }
            SavingsAccount savingsAccount = new SavingsAccount(request.getCustomer(), accountNumber);
            accountRepository.save(savingsAccount);
        }
        else if(request instanceof CreditRequest creditReq){
            Optional<CreditCard> existingCard = creditCardRepository.findByCustomerId(request.getCustomer().getId());

            if(existingCard.isPresent()){
                CreditCard creditCard = existingCard.get();
                creditCard.setCreditLimit(creditReq.getRequestedLimit());
                creditCardRepository.save(creditCard);
            }else{
                String cardNumber = generateCardNumber();
                while (creditCardRepository.existsByCardNumber( cardNumber)){
                    cardNumber = generateCardNumber();
                }
                CreditCard newCreditCard = new CreditCard(creditReq.getRequestedLimit(), cardNumber, creditReq.getCustomer());
                creditCardRepository.save(newCreditCard);
            }

        }
        request.setStatus(RequestStatus.APPROVED);
        Request updatedRequest = requestRepository.save(request);

        return RequestResponseDTO.fromEntity(updatedRequest);

    }

    @Transactional
    public RequestResponseDTO rejectRequest(Long requestId, Long managerId){
        if(!managerRepository.existsById(managerId)){
            throw new RuntimeException("Id de Gerente inválido");
        }
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Id da solicitação inválido"));

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new RuntimeException("Apenas solicitações com status PENDENTE podem ser negadas");
        }

        request.setStatus(RequestStatus.REJECTED);
        Request updatedRequest = requestRepository.save(request);

        return RequestResponseDTO.fromEntity(updatedRequest);

    }

    @Transactional
    public List<RequestResponseDTO> getRequestsByCustomer(Long customerId){
        List<Request> requests = requestRepository.findByCustomerId(customerId);
        List<RequestResponseDTO> requestsDTO = new ArrayList<>();

        for(Request request : requests){
            requestsDTO.add(RequestResponseDTO.fromEntity(request));
        }
        return requestsDTO;
    }


}
