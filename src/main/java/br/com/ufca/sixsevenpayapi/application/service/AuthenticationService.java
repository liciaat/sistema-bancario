package br.com.ufca.sixsevenpayapi.application.service;

import br.com.ufca.sixsevenpayapi.application.dto.LoginRequestDTO;
import br.com.ufca.sixsevenpayapi.application.dto.RegisterRequestDTO;
import br.com.ufca.sixsevenpayapi.application.dto.UpdatePasswordDTO;
import br.com.ufca.sixsevenpayapi.application.dto.UserResponseDTO;
import br.com.ufca.sixsevenpayapi.application.dto.DeleteOwnAccountRequestDTO;
import br.com.ufca.sixsevenpayapi.domain.entity.*;
import br.com.ufca.sixsevenpayapi.domain.enums.AccountStatus;
import br.com.ufca.sixsevenpayapi.domain.enums.RequestStatus;
import br.com.ufca.sixsevenpayapi.domain.enums.RequestType;
import br.com.ufca.sixsevenpayapi.domain.utils.EmailValidator;
import br.com.ufca.sixsevenpayapi.domain.utils.PhoneValidator;
import br.com.ufca.sixsevenpayapi.repository.AccountRepository;
import br.com.ufca.sixsevenpayapi.repository.RequestRepository;
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
    private final RequestRepository requestRepository;

    public AuthenticationService(UserRepository userRepository, AccountRepository accountRepository, RequestRepository requestRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.requestRepository = requestRepository;
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
    public void deleteOwnAccount(DeleteOwnAccountRequestDTO dto){
        User user = userRepository.findById(dto.accountId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if(!dto.password().equals(user.getPassword())){
            throw new RuntimeException("Senha incorreta");
        }

        if (user instanceof Customer customer) {
            if(requestRepository.existsByCustomerIdAndTypeAndStatus(customer.getId(), RequestType.CLOSURE, RequestStatus.PENDING)){
                throw new RuntimeException("Já existe uma solicitação de encerramento pendente para esta conta");
            }

            ClosureRequest request = new ClosureRequest(customer);
            requestRepository.save(request);
        }else {
            throw new RuntimeException("Apenas cliente podem solicitar encerramento da conta por este canal");
        }

    }


}
