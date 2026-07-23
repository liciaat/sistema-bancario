package br.com.ufca.sixsevenpayapi.application.service;

import br.com.ufca.sixsevenpayapi.application.dto.DepositDTO;
import br.com.ufca.sixsevenpayapi.application.dto.TransactionResponseDTO;
import br.com.ufca.sixsevenpayapi.application.dto.TransferDTO;
import br.com.ufca.sixsevenpayapi.application.dto.WithdrawDTO;
import br.com.ufca.sixsevenpayapi.domain.entity.Account;
import br.com.ufca.sixsevenpayapi.domain.entity.Transaction;
import br.com.ufca.sixsevenpayapi.domain.enums.AccountStatus;
import br.com.ufca.sixsevenpayapi.domain.enums.TransactionType;
import br.com.ufca.sixsevenpayapi.repository.AccountRepository;
import br.com.ufca.sixsevenpayapi.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransactionResponseDTO deposit(DepositDTO dto){
        Account account = accountRepository.findByAccountNumber(dto.accountNumber())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Operação não permitida: A conta está bloqueada ou inativa.");
        }

        account.setBalance(account.getBalance().add(dto.amount()));
        accountRepository.save(account);

        Transaction transaction = new Transaction(account, dto.amount(), TransactionType.DEPOSIT);
        transactionRepository.save(transaction);
        return TransactionResponseDTO.fromEntity(transaction);

    }

    @Transactional
    public TransactionResponseDTO transferBetweenOwnAccount(TransferDTO transferDTO){
        Account sourceAccount = accountRepository.findByAccountNumber(transferDTO.sourceAccountNumber())
                .orElseThrow(()->new RuntimeException("Conta origem não encontrada"));


        if (sourceAccount.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Operação não permitida: A conta origem está bloqueada ou inativa.");
        }

        if(!transferDTO.transactionPassword().equals(sourceAccount.getCustomer().getTransactionPassword())){
            throw new RuntimeException("Senha de transação incorreta!");
        }

        Account targetAccount = accountRepository.findByAccountNumber(transferDTO.targetAccountNumber())
                .orElseThrow(()->new RuntimeException("Conta destino não encontrada"));


        if (targetAccount.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Operação não permitida: A conta destino está bloqueada ou inativa.");
        }

        if(targetAccount.getCustomer().equals(sourceAccount.getCustomer())){
            return this.transfer(transferDTO);
        }

        if(sourceAccount.getBalance().compareTo(transferDTO.amount())< 0){
            throw new RuntimeException("Saldo insuficiente");
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(transferDTO.amount()));
        accountRepository.save(sourceAccount);
        targetAccount.setBalance(targetAccount.getBalance().add(transferDTO.amount()));
        accountRepository.save(targetAccount);

        Transaction sourceTransaction = new Transaction(sourceAccount, transferDTO.amount().negate(), TransactionType.TRANSFER);
        transactionRepository.save(sourceTransaction);

        Transaction targetTransaction = new Transaction(targetAccount, transferDTO.amount(), TransactionType.TRANSFER);
        transactionRepository.save(targetTransaction);

        return TransactionResponseDTO.fromEntity(sourceTransaction);
    }

    @Transactional
    public TransactionResponseDTO withdraw(WithdrawDTO dto){
        Account account = accountRepository.findByAccountNumber(dto.accountNumber())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));


        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Operação não permitida: A conta está bloqueada ou inativa.");
        }

        if(!dto.transactionPassword().equals(account.getCustomer().getTransactionPassword())){
            throw new RuntimeException("Senha de transação incorreta!");
        }

        if(account.getBalance().compareTo(dto.amount()) < 0){
            throw new RuntimeException("Saldo insuficiente para saque");
        }


        account.setBalance(account.getBalance().subtract(dto.amount()));
        accountRepository.save(account);
        Transaction transaction = new Transaction(account, dto.amount().negate(), TransactionType.WITHDRAW);
        transactionRepository.save(transaction);
        return TransactionResponseDTO.fromEntity(transaction);


    }

    @Transactional
    public TransactionResponseDTO transfer(TransferDTO dto){
        if(dto.targetAccountNumber().equals(dto.sourceAccountNumber())){
            throw new RuntimeException("A conta de origem não pode ser igual a conta destino");
        }

        Account sourceAccount = accountRepository.findByAccountNumber(dto.sourceAccountNumber())
                .orElseThrow(() -> new RuntimeException("Conta Origem não encontrada"));

        if (sourceAccount.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Operação não permitida: A conta origem está bloqueada ou inativa.");
        }

        if(!dto.transactionPassword().equals(sourceAccount.getCustomer().getTransactionPassword())){
            throw new RuntimeException("Senha de transação incorreta!");
        }

        if(sourceAccount.getBalance().compareTo(dto.amount()) < 0){
            throw new RuntimeException("Saldo insuficiente para transferencia");
        }

        Account targetAccount = accountRepository.findByAccountNumber(dto.targetAccountNumber())
                .orElseThrow(() -> new RuntimeException("Conta Destino não encontrada"));

        if (targetAccount.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new RuntimeException("Operação não permitida: A conta destino está bloqueada ou inativa.");
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(dto.amount()));
        accountRepository.save(sourceAccount);
        targetAccount.setBalance(targetAccount.getBalance().add(dto.amount()));
        accountRepository.save(targetAccount);

        Transaction sourceTransaction = new Transaction(sourceAccount, dto.amount().negate(), TransactionType.TRANSFER);
        transactionRepository.save(sourceTransaction);
        Transaction targetTransaction = new Transaction(targetAccount, dto.amount(), TransactionType.TRANSFER);
        transactionRepository.save(targetTransaction);

        return TransactionResponseDTO.fromEntity(sourceTransaction);
    }


    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getTransactionHistory(String accountNumber){
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        List<Transaction> transactions = transactionRepository.findByAccountIdOrderByCreatedAtDesc(account.getId());
        List<TransactionResponseDTO> dtos = new ArrayList<>();
        for(Transaction transaction : transactions){
            dtos.add(TransactionResponseDTO.fromEntity(transaction));
        }
        return dtos;
    }

}
