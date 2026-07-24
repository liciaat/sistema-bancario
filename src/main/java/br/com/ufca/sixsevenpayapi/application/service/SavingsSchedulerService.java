package br.com.ufca.sixsevenpayapi.application.service;

import br.com.ufca.sixsevenpayapi.domain.entity.Account;
import br.com.ufca.sixsevenpayapi.domain.entity.SystemConfig;
import br.com.ufca.sixsevenpayapi.domain.entity.Transaction;
import br.com.ufca.sixsevenpayapi.domain.enums.AccountStatus;
import br.com.ufca.sixsevenpayapi.domain.enums.AccountType;
import br.com.ufca.sixsevenpayapi.domain.enums.TransactionType;
import br.com.ufca.sixsevenpayapi.repository.AccountRepository;
import br.com.ufca.sixsevenpayapi.repository.SystemConfigRepository;
import br.com.ufca.sixsevenpayapi.repository.TransactionRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.util.List;

@Service
public class SavingsSchedulerService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final SystemConfigRepository systemConfigRepository;

    public SavingsSchedulerService(AccountRepository accountRepository, TransactionRepository transactionRepository, SystemConfigRepository systemConfigRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.systemConfigRepository = systemConfigRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void processMonthlySavingsYield(){
        YearMonth currentMonth = YearMonth.now();
        SystemConfig config = systemConfigRepository.findAll().stream().findFirst().orElse(null);

        if(config == null || config.getSavingsInterestRate().compareTo(BigDecimal.ZERO) == 0){
            return;
        }

        if(config.getLastYieldProcessedMonth() != null && config.getLastYieldProcessedMonth().equals(currentMonth)){
            return;
        }

        BigDecimal rateMultiplier = config.getSavingsInterestRate();

        List<Account> savingsAccounts = accountRepository.findByAccountTypeAndAccountStatus(AccountType.SAVINGS, AccountStatus.ACTIVE);
        for(Account account : savingsAccounts){
            BigDecimal currentBalance = account.getBalance();

            if(currentBalance.compareTo(BigDecimal.ZERO) > 0){
                BigDecimal yieldAmount = currentBalance.multiply(rateMultiplier).setScale(2, RoundingMode.HALF_UP);
                account.setBalance(account.getBalance().add(yieldAmount));
                accountRepository.save(account);

                Transaction transaction = new Transaction(account, yieldAmount, TransactionType.YIELD);
                transactionRepository.save(transaction);
            }
        }
        config.setLastYieldProcessedMonth(currentMonth);
        systemConfigRepository.save(config);

    }

}
