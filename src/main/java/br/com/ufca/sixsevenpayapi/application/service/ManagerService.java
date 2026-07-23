package br.com.ufca.sixsevenpayapi.application.service;


import br.com.ufca.sixsevenpayapi.application.dto.AccountRequestResponseDTO;
import br.com.ufca.sixsevenpayapi.application.dto.AccountResponseDTO;
import br.com.ufca.sixsevenpayapi.domain.entity.AccountRequest;
import br.com.ufca.sixsevenpayapi.repository.AccountRepository;
import br.com.ufca.sixsevenpayapi.repository.AccountRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ManagerService {

    private final AccountRequestRepository accountRequestRepository;
    private final AccountRepository accountRepository;

    public ManagerService(AccountRequestRepository accountRequestRepository, AccountRepository accountRepository) {
        this.accountRequestRepository = accountRequestRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public List<AccountRequestResponseDTO> getPendingRequests(){
        List<AccountRequest> accountRequests = accountRequestRepository.findAll();
        List<AccountRequestResponseDTO> accountRequestResponseDTOs = new ArrayList<>();
        for(AccountRequest accountRequest : accountRequests){
            accountRequestResponseDTOs.add(AccountRequestResponseDTO.fromEntity(accountRequest));
        }
        return accountRequestResponseDTOs;
    }


}
