package br.com.ufca.sixsevenpayapi.application.service;

import br.com.ufca.sixsevenpayapi.application.dto.LoginRequestDTO;
import br.com.ufca.sixsevenpayapi.application.dto.RegisterRequestDTO;
import br.com.ufca.sixsevenpayapi.application.dto.UpdatePasswordDTO;
import br.com.ufca.sixsevenpayapi.application.dto.UserResponseDTO;
import br.com.ufca.sixsevenpayapi.domain.entity.Account;
import br.com.ufca.sixsevenpayapi.domain.entity.CheckingAccount;
import br.com.ufca.sixsevenpayapi.domain.entity.Customer;
import br.com.ufca.sixsevenpayapi.domain.entity.User;
import br.com.ufca.sixsevenpayapi.domain.enums.AccountStatus;
import br.com.ufca.sixsevenpayapi.domain.utils.EmailValidator;
import br.com.ufca.sixsevenpayapi.domain.utils.PhoneValidator;
import br.com.ufca.sixsevenpayapi.repository.AccountRepository;
import br.com.ufca.sixsevenpayapi.repository.UserRepository;
import org.springframework.stereotype.Service;
import br.com.ufca.sixsevenpayapi.domain.utils.CpfValidator;
import org.springframework.transaction.annotation.Transactional;
import static br.com.ufca.sixsevenpayapi.domain.utils.GenerateNumber.generateAccountNumber;

import java.math.BigDecimal;


@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    public AuthenticationService(UserRepository userRepository, AccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public UserResponseDTO login(LoginRequestDTO dto) {

        String clearCpf = CpfValidator.validateAndSanitizeCpf(dto.cpf());
        User user = userRepository.findByCpf(clearCpf);


        if(user == null){
            throw new RuntimeException("Usuário não existe");
        }
        if(!user.isActive()){
            throw new RuntimeException("Usuário não está  ativo");
        }
        if(!dto.password().equals(user.getPassword())){
            throw new RuntimeException("Senha incorreta");
        }
        return UserResponseDTO.fromEntity(user);
    }

    @Transactional
    public UserResponseDTO register(RegisterRequestDTO dto) {
        String cleanCpf = CpfValidator.validateAndSanitizeCpf(dto.cpf());
        if(userRepository.existsByCpf(cleanCpf)){
            throw new RuntimeException("Usuário já cadastrado");
        }
        String cleanEmail = EmailValidator.validateAndSanitizeEmail(dto.email());
        if(userRepository.existsByEmail(cleanEmail)){
            throw new RuntimeException("Email já cadastrado");
        }
        String cleanPhone = PhoneValidator.validateAndSanitizePhone(dto.phoneNumber());
        if(userRepository.existsByPhone(cleanPhone)){
            throw new RuntimeException("Telefone já cadastrado");
        }

        if(!dto.password().equals(dto.confirmPassword())){
            throw new RuntimeException("Senhas não coincidem");
        }

        if(!dto.confirmTransactionPassword().equals(dto.transactionPassword())){
            throw new RuntimeException("Senhas de transação não coincidem");

        }

        Customer customer = new Customer(dto.name(), cleanCpf, cleanEmail, dto.password(), cleanPhone, dto.transactionPassword());
        Customer savedCustomer = userRepository.save(customer);


        String accountNumber = generateAccountNumber();
        while (accountRepository.existsByAccountNumber(accountNumber)){
            accountNumber = generateAccountNumber();
        }

        Account initialAccount = new CheckingAccount(savedCustomer, accountNumber);
        accountRepository.save(initialAccount);

        return UserResponseDTO.fromEntity(savedCustomer);
    }

    @Transactional
    public void updatePassword(UpdatePasswordDTO dto){
        String clearCpf = CpfValidator.validateAndSanitizeCpf(dto.cpf());
        User user = userRepository.findByCpf(clearCpf);
        if(user == null){
            throw new RuntimeException("Usuário não existe");
        }
        if(!dto.password().equals(user.getPassword())){
            throw new RuntimeException("Senha incorreta");
        }
        if(dto.password().equals(dto.newPassword())){
            throw new RuntimeException("Senha igual a anterior");
        }
        user.changePassword(dto.newPassword());
    }

    @Transactional
    public void deleteOwnAccount(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if (user instanceof Customer customer) {
            if (customer.getAccounts() != null && !customer.getAccounts().isEmpty()) {
                if(customer.getCreditCard() != null && customer.getCreditCard().getCurrentSpending().compareTo(BigDecimal.ZERO) > 0){
                    throw new RuntimeException("Não é possível excluir o perfil: ainda existe divida no cartão de crédito");
                }

                for (Account account : customer.getAccounts()) {
                    if (account.getBalance() != null && account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
                        throw new RuntimeException("Não é possível excluir o perfil: ainda existe saldo na conta " + account.getAccountNumber());
                    }
                    account.setAccountStatus(AccountStatus.CLOSED);
                }
            }
        }
        user.deactivate();
    }


}
