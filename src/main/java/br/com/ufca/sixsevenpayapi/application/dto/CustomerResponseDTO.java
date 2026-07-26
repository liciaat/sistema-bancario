package br.com.ufca.sixsevenpayapi.application.dto;

import br.com.ufca.sixsevenpayapi.domain.entity.Account;
import br.com.ufca.sixsevenpayapi.domain.entity.Customer;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public record CustomerResponseDTO (
        long id,
        String Name,
        String cpf,
        String email,
        String phone,
        List<AccountResponseDTO> accounts
){
    public static CustomerResponseDTO fromEntity(Customer customer) {

        List<Account> accounts =  customer.getAccounts();
        List<AccountResponseDTO> accountsDTO = new ArrayList<>();
        for (Account account : accounts) {
            accountsDTO.add(AccountResponseDTO.fromEntity(account));
        }
        return new CustomerResponseDTO (
                customer.getId(),
                customer.getName(),
                customer.getCpf(),
                customer.getEmail(),
                customer.getPhone(),
                accountsDTO
        );
    }


}

