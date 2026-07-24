package br.com.ufca.sixsevenpayapi.application.service;

import br.com.ufca.sixsevenpayapi.application.dto.DashboardResponseDTO;
import br.com.ufca.sixsevenpayapi.application.dto.RegisterManagerDTO;
import br.com.ufca.sixsevenpayapi.application.dto.UserResponseDTO;
import br.com.ufca.sixsevenpayapi.domain.entity.Manager;
import br.com.ufca.sixsevenpayapi.domain.enums.AccountStatus;
import br.com.ufca.sixsevenpayapi.domain.enums.RequestStatus;
import br.com.ufca.sixsevenpayapi.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static br.com.ufca.sixsevenpayapi.domain.utils.CpfValidator.validateAndSanitizeCpf;
import static br.com.ufca.sixsevenpayapi.domain.utils.EmailValidator.validateAndSanitizeEmail;
import static br.com.ufca.sixsevenpayapi.domain.utils.PhoneValidator.validateAndSanitizePhone;

@Service
public class AdminService {

    private final ManagerRepository managerRepository;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final RequestRepository requestRepository;

    public AdminService(ManagerRepository managerRepository, UserRepository userRepository, AccountRepository accountRepository, CustomerRepository customerRepository, RequestRepository requestRepository) {
        this.managerRepository = managerRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.requestRepository = requestRepository;
    }

    @Transactional
    public UserResponseDTO registerManager(RegisterManagerDTO dto){
        String cleanCpf = validateAndSanitizeCpf(dto.cpf());
        if(userRepository.existsByCpf(cleanCpf)){
            throw new RuntimeException("Usuário já cadastrado com este CPF");
        }

        String cleanEmail = validateAndSanitizeEmail(dto.email());
        if(userRepository.existsByEmail(cleanEmail)){
            throw new RuntimeException("Email já cadastrado");
        }

        String cleanPhone = validateAndSanitizePhone(dto.phone());

        Manager manager = new Manager(dto.name(), cleanCpf,cleanEmail,cleanPhone, dto.password(), dto.registration(), LocalDate.now());
        Manager savedManager = managerRepository.save(manager);
        return UserResponseDTO.fromEntity(savedManager);
    }

    @Transactional
    public void removeManager(Long managerId){
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Gerente não encontrado"));
        manager.deactivate();
        managerRepository.save(manager);
    }

    @Transactional(readOnly = true)
    public DashboardResponseDTO getDashboardMetrics(){
        long totalAccounts = accountRepository.count();
        long totalCustomers = customerRepository.count();
        BigDecimal totalBankBalance = accountRepository.getTotalBankBalance();
        if(totalBankBalance == null) totalBankBalance = BigDecimal.ZERO;

        long blockedAccounts = accountRepository.countByAccountStatus(AccountStatus.BLOCKED);
        long pendingRequests = requestRepository.countByStatus(RequestStatus.PENDING);

        return new DashboardResponseDTO(totalAccounts, totalCustomers, totalBankBalance, blockedAccounts, pendingRequests);
    }

}
