package br.com.ufca.sixsevenpayapi.application.service;


import br.com.ufca.sixsevenpayapi.application.dto.*;
import br.com.ufca.sixsevenpayapi.domain.entity.*;
import br.com.ufca.sixsevenpayapi.domain.enums.AccountStatus;
import br.com.ufca.sixsevenpayapi.domain.enums.RequestStatus;
import br.com.ufca.sixsevenpayapi.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ManagerService {

    private final RequestRepository requestRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final ManagerRepository managerRepository;
    private final CustomerRepository customerRepository;

    public ManagerService(RequestRepository requestRepository, CustomerRepository customerRepository,AccountRepository accountRepository, TransactionRepository transactionRepository, ManagerRepository managerRepository) {
        this.requestRepository = requestRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.managerRepository = managerRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public List<RequestResponseDTO> listPendingRequests() {
        List<Request> requests = requestRepository.findByStatus(RequestStatus.PENDING);
        List<RequestResponseDTO> responseDTOs = new ArrayList<>();
        for (Request request : requests) {
            responseDTOs.add(RequestResponseDTO.fromEntity(request));
        }
        return responseDTOs;
    }

    @Transactional
    public AccountResponseDTO toggleAccountStatus(Long accountId){
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new br.com.ufca.sixsevenpayapi.common.exception.NotFoundException("Conta não encontrada"));

        if(account.getAccountStatus() == AccountStatus.CLOSED){
            throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException("Não é possível alterar o status de uma conta fechada");
        }

        if(account.getAccountStatus() == AccountStatus.BLOCKED){
            account.setAccountStatus(AccountStatus.ACTIVE);
        }else {
            account.setAccountStatus(AccountStatus.BLOCKED);
        }

        accountRepository.save(account);
        return AccountResponseDTO.fromEntity(account);

    }

    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getGeneralTransactions() {
        List<Transaction> transactions = transactionRepository.findAllByOrderByCreatedAtDesc();
        List<TransactionResponseDTO> responseDTOs = new ArrayList<>();
        for (Transaction transaction : transactions) {
            responseDTOs.add(TransactionResponseDTO.fromEntity(transaction));
        }
        return responseDTOs;
    }

    @Transactional(readOnly = true)
    public List<AccountResponseDTO> getNegativeAccounts() {
        List<Account> negativeAccounts = accountRepository.findByBalanceLessThan(BigDecimal.ZERO);
        List<AccountResponseDTO> responseDTOs = new ArrayList<>();
        for (Account negativeAccount : negativeAccounts) {
            responseDTOs.add(AccountResponseDTO.fromEntity(negativeAccount));
        }
        return responseDTOs;
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getManager(Long managerId){
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new br.com.ufca.sixsevenpayapi.common.exception.NotFoundException("Gerente não encontrado"));
        return UserResponseDTO.fromEntity(manager);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> getCustomers() {
        List<Customer> customers = customerRepository.findAll();
        List<CustomerResponseDTO> responseDTOs = new ArrayList<>();
        for (Customer customer : customers) {
            responseDTOs.add(CustomerResponseDTO.fromEntity(customer));
        }
        return responseDTOs;
    }


}
