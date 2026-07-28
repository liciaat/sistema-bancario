package br.com.ufca.sixsevenpayapi.application.service;

import br.com.ufca.sixsevenpayapi.application.dto.*;
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
    public TransactionResponseDTO deposit(Long accountId, DepositDTO dto){
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new br.com.ufca.sixsevenpayapi.common.exception.NotFoundException("Conta não encontrada"));

        if (!account.getCustomer().isActive()) {
            throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException("Operação negada: O cliente titular está inativo.");
        }

        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException("Operação não permitida: A conta está bloqueada ou inativa.");
        }

        account.deposit(dto.amount());
        accountRepository.save(account);

        Transaction transaction = new Transaction(account, dto.amount(), TransactionType.DEPOSIT);
        transactionRepository.save(transaction);
        return TransactionResponseDTO.fromEntity(transaction);

    }

    @Transactional
    public TransactionResponseDTO transferBetweenOwnAccount(Long sourceAccountId, TransferDTO transferDTO){
        Account sourceAccount = accountRepository.findById(sourceAccountId)
                .orElseThrow(()->new br.com.ufca.sixsevenpayapi.common.exception.NotFoundException("Conta origem não encontrada"));

        if (!sourceAccount.getCustomer().isActive()) {
            throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException("Operação negada: O cliente titular está inativo.");
        }

        if (sourceAccount.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException("Operação não permitida: A conta origem está bloqueada ou inativa.");
        }

        if(!transferDTO.transactionPassword().equals(sourceAccount.getCustomer().getTransactionPassword())){
            throw new br.com.ufca.sixsevenpayapi.common.exception.UnauthorizedException("Senha de transação incorreta!");
        }

        Account targetAccount = accountRepository.findByAccountNumber(transferDTO.targetAccountNumber())
                .orElseThrow(()->new br.com.ufca.sixsevenpayapi.common.exception.NotFoundException("Conta destino não encontrada"));


        if (targetAccount.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException("Operação não permitida: A conta destino está bloqueada ou inativa.");
        }

        if (!targetAccount.getCustomer().getId().equals(sourceAccount.getCustomer().getId())) {
            throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException("A conta destino deve pertencer ao mesmo titular");
        }
        return this.transfer(sourceAccountId, transferDTO);
    }

    @Transactional
    public TransactionResponseDTO withdraw(Long accountId, WithdrawDTO dto){
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new br.com.ufca.sixsevenpayapi.common.exception.NotFoundException("Conta não encontrada"));

        if (!account.getCustomer().isActive()) {
            throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException("Operação negada: O cliente titular está inativo.");
        }

        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException("Operação não permitida: A conta está bloqueada ou inativa.");
        }

        if(!dto.transactionPassword().equals(account.getCustomer().getTransactionPassword())){
            throw new br.com.ufca.sixsevenpayapi.common.exception.UnauthorizedException("Senha de transação incorreta!");
        }

        account.withdraw(dto.amount());
        accountRepository.save(account);
        Transaction transaction = new Transaction(account, dto.amount().negate(), TransactionType.WITHDRAW);
        transactionRepository.save(transaction);
        return TransactionResponseDTO.fromEntity(transaction);


    }

    @Transactional
    public TransactionResponseDTO transfer(Long sourceAccountId, TransferDTO dto){
        Account sourceAccount = accountRepository.findById(sourceAccountId)
                .orElseThrow(() -> new br.com.ufca.sixsevenpayapi.common.exception.NotFoundException("Conta Origem não encontrada"));

        if(dto.targetAccountNumber().equals(sourceAccount.getAccountNumber())){
            throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException("A conta de origem não pode ser igual a conta destino");
        }


        if (!sourceAccount.getCustomer().isActive()) {
            throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException("Operação negada: O cliente titular da conta origem está inativo.");
        }

        if (sourceAccount.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException("Operação não permitida: A conta origem está bloqueada ou inativa.");
        }

        if(!dto.transactionPassword().equals(sourceAccount.getCustomer().getTransactionPassword())){
            throw new br.com.ufca.sixsevenpayapi.common.exception.UnauthorizedException("Senha de transação incorreta!");
        }

        if (!sourceAccount.canDebit(dto.amount())) {
            throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException(
                    "Saldo insuficiente. O valor ultrapassa o limite da conta.");
        }

        Account targetAccount = accountRepository.findByAccountNumber(dto.targetAccountNumber())
                .orElseThrow(() -> new br.com.ufca.sixsevenpayapi.common.exception.NotFoundException("Conta Destino não encontrada"));

        if (!targetAccount.getCustomer().isActive()) {
            throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException("Operação negada: O cliente titular da conta destino está inativo.");
        }

        if (targetAccount.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException("Operação não permitida: A conta destino está bloqueada ou inativa.");
        }

        sourceAccount.debit(dto.amount());
        accountRepository.save(sourceAccount);
        targetAccount.deposit(dto.amount());
        accountRepository.save(targetAccount);

        Transaction sourceTransaction = new Transaction(sourceAccount, dto.amount().negate(), TransactionType.TRANSFER);
        transactionRepository.save(sourceTransaction);
        Transaction targetTransaction = new Transaction(targetAccount, dto.amount(), TransactionType.TRANSFER);
        transactionRepository.save(targetTransaction);

        return TransactionResponseDTO.fromEntity(sourceTransaction);
    }


    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getTransactionHistory(Long accountId){
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new br.com.ufca.sixsevenpayapi.common.exception.NotFoundException("Conta não encontrada"));

        List<Transaction> transactions = transactionRepository.findByAccountIdOrderByCreatedAtDesc(account.getId());
        List<TransactionResponseDTO> dtos = new ArrayList<>();
        for(Transaction transaction : transactions){
            dtos.add(TransactionResponseDTO.fromEntity(transaction));
        }
        return dtos;
    }

    @Transactional(readOnly = true)
    public AccountResponseDTO getAccountByNumber(String accountNumber){
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new br.com.ufca.sixsevenpayapi.common.exception.NotFoundException("Conta não encontrada"));
        return AccountResponseDTO.fromEntity(account);
    }

    @Transactional(readOnly = true)
    public BalanceResponseDTO getBalance(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new br.com.ufca.sixsevenpayapi.common.exception.NotFoundException("Conta não encontrada"));

        return new BalanceResponseDTO(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance()
        );
    }

}
