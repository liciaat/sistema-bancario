package br.com.ufca.sixsevenpayapi.application.service;


import br.com.ufca.sixsevenpayapi.application.dto.*;
import br.com.ufca.sixsevenpayapi.domain.entity.Account;
import br.com.ufca.sixsevenpayapi.domain.entity.AccountRequest;
import br.com.ufca.sixsevenpayapi.domain.entity.Request;
import br.com.ufca.sixsevenpayapi.domain.entity.Transaction;
import br.com.ufca.sixsevenpayapi.domain.enums.AccountStatus;
import br.com.ufca.sixsevenpayapi.domain.enums.RequestStatus;
import br.com.ufca.sixsevenpayapi.repository.AccountRepository;
import br.com.ufca.sixsevenpayapi.repository.AccountRequestRepository;
import br.com.ufca.sixsevenpayapi.repository.RequestRepository;
import br.com.ufca.sixsevenpayapi.repository.TransactionRepository;
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

    public ManagerService(RequestRepository requestRepository, AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.requestRepository = requestRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
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
    public AccountResponseDTO toggleAccountStatus(ToggleAccountBlockDTO dto){
        Account account = accountRepository.findByAccountNumber(dto.accountId())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        if(account.getAccountStatus() == AccountStatus.CLOSED){
            throw new RuntimeException("Não é possível alterar o status de uma conta fechada");
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


}
