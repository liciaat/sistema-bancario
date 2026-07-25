package br.com.ufca.sixsevenpayapi.application.service;

import br.com.ufca.sixsevenpayapi.application.dto.DepositDTO;
import br.com.ufca.sixsevenpayapi.application.dto.TransactionResponseDTO;
import br.com.ufca.sixsevenpayapi.application.dto.TransferDTO;
import br.com.ufca.sixsevenpayapi.application.dto.WithdrawDTO;
import br.com.ufca.sixsevenpayapi.domain.entity.Account;
import br.com.ufca.sixsevenpayapi.domain.entity.Transaction;
import br.com.ufca.sixsevenpayapi.domain.enums.AccountStatus;
import br.com.ufca.sixsevenpayapi.domain.enums.AccountType;
import br.com.ufca.sixsevenpayapi.domain.enums.TransactionType;
import br.com.ufca.sixsevenpayapi.repository.AccountRepository;
import br.com.ufca.sixsevenpayapi.repository.TransactionRepository;
import br.com.ufca.sixsevenpayapi.repository.CustomerRepository;
import br.com.ufca.sixsevenpayapi.repository.UserRepository;
import br.com.ufca.sixsevenpayapi.domain.utils.CpfValidator;
import br.com.ufca.sixsevenpayapi.application.dto.AccountResponseDTO;
import br.com.ufca.sixsevenpayapi.domain.entity.Customer;
import br.com.ufca.sixsevenpayapi.domain.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    private static final BigDecimal OVERDRAFT_LIMIT = new BigDecimal("500");

    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository, CustomerRepository customerRepository, UserRepository userRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
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

        account.setBalance(account.getBalance().add(dto.amount()));
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

        Account targetAccount = accountRepository.findById(transferDTO.targetAccountId())
                .orElseThrow(()->new br.com.ufca.sixsevenpayapi.common.exception.NotFoundException("Conta destino não encontrada"));


        if (targetAccount.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException("Operação não permitida: A conta destino está bloqueada ou inativa.");
        }

        if(targetAccount.getCustomer().equals(sourceAccount.getCustomer())){
            return this.transfer(sourceAccountId, transferDTO);
        }

        checkAvailableLimit(sourceAccount, transferDTO.amount());

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

        if(account.getBalance().compareTo(dto.amount()) < 0){
            throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException("Saldo insuficiente para saque");
        }


        account.setBalance(account.getBalance().subtract(dto.amount()));
        accountRepository.save(account);
        Transaction transaction = new Transaction(account, dto.amount().negate(), TransactionType.WITHDRAW);
        transactionRepository.save(transaction);
        return TransactionResponseDTO.fromEntity(transaction);


    }

    @Transactional
    public TransactionResponseDTO transfer(Long sourceAccountId, TransferDTO dto){
        if(dto.targetAccountId().equals(sourceAccountId)){
            throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException("A conta de origem não pode ser igual a conta destino");
        }

        Account sourceAccount = accountRepository.findById(sourceAccountId)
                .orElseThrow(() -> new br.com.ufca.sixsevenpayapi.common.exception.NotFoundException("Conta Origem não encontrada"));

        if (!sourceAccount.getCustomer().isActive()) {
            throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException("Operação negada: O cliente titular da conta origem está inativo.");
        }

        if (sourceAccount.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException("Operação não permitida: A conta origem está bloqueada ou inativa.");
        }

        if(!dto.transactionPassword().equals(sourceAccount.getCustomer().getTransactionPassword())){
            throw new br.com.ufca.sixsevenpayapi.common.exception.UnauthorizedException("Senha de transação incorreta!");
        }

        checkAvailableLimit(sourceAccount, dto.amount());


        Account targetAccount = accountRepository.findById(dto.targetAccountId())
                .orElseThrow(() -> new br.com.ufca.sixsevenpayapi.common.exception.NotFoundException("Conta Destino não encontrada"));

        if (!targetAccount.getCustomer().isActive()) {
            throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException("Operação negada: O cliente titular da conta destino está inativo.");
        }

        if (targetAccount.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException("Operação não permitida: A conta destino está bloqueada ou inativa.");
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

    private void checkAvailableLimit(Account account, BigDecimal requestedAmount){
        BigDecimal availableLimit = account.getBalance();

        if(account.getAccountType() == AccountType.CHECKING){
            availableLimit = availableLimit.add(OVERDRAFT_LIMIT);
        }
        if(availableLimit.compareTo(requestedAmount) < 0){
            throw new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException("Saldo insuficiente. O valor ultrapassa o limite da conta.");
        }
    }

}
