package br.com.ufca.sixsevenpayapi.application.dto;

import br.com.ufca.sixsevenpayapi.domain.entity.Account;
import br.com.ufca.sixsevenpayapi.domain.entity.CheckingAccount;
import br.com.ufca.sixsevenpayapi.domain.enums.AccountType;

import java.math.BigDecimal;

public record AccountResponseDTO(
        Long id,
        String accountNumber,
        BigDecimal balance,
        boolean active,
        AccountType accountType,
        String name
) {
    public static AccountResponseDTO fromEntity(Account account) {
        if(account instanceof CheckingAccount){
            AccountType accountType = AccountType.CHECKING;
        }else {
            AccountType accountType = AccountType.SAVINGS;
        }

        return new AccountResponseDTO(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getCustomer().isActive(),
                accountType,
                account.getCustomer().getName()
        );

    }

}
