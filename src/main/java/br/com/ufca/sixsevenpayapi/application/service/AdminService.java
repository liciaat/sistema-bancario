package br.com.ufca.sixsevenpayapi.application.service;

import br.com.ufca.sixsevenpayapi.application.dto.DashboardResponseDTO;
import br.com.ufca.sixsevenpayapi.application.dto.RegisterManagerDTO;
import br.com.ufca.sixsevenpayapi.application.dto.UpdateManagerDTO;
import br.com.ufca.sixsevenpayapi.application.dto.UserResponseDTO;
import br.com.ufca.sixsevenpayapi.domain.entity.Manager;
import br.com.ufca.sixsevenpayapi.domain.entity.SystemConfig;
import br.com.ufca.sixsevenpayapi.domain.enums.AccountStatus;
import br.com.ufca.sixsevenpayapi.domain.enums.RequestStatus;
import br.com.ufca.sixsevenpayapi.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
    private final SystemConfigRepository systemConfigRepository;

    public AdminService(ManagerRepository managerRepository, SystemConfigRepository systemConfigRepository,UserRepository userRepository, AccountRepository accountRepository, CustomerRepository customerRepository, RequestRepository requestRepository) {
        this.managerRepository = managerRepository;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
        this.requestRepository = requestRepository;
        this.systemConfigRepository = systemConfigRepository;
    }

    @Transactional
    public UserResponseDTO registerManager(RegisterManagerDTO dto){
        String cleanCpf = validateAndSanitizeCpf(dto.cpf());
        if(userRepository.existsByCpf(cleanCpf)){
            throw new br.com.ufca.sixsevenpayapi.common.exception.ConflictException("Usuário já cadastrado com este CPF");
        }

        String cleanEmail = validateAndSanitizeEmail(dto.email());
        if(userRepository.existsByEmail(cleanEmail)){
            throw new br.com.ufca.sixsevenpayapi.common.exception.ConflictException("Email já cadastrado");
        }

        String cleanPhone = validateAndSanitizePhone(dto.phone());

        Manager manager = new Manager(dto.name(), cleanCpf,cleanEmail,cleanPhone, dto.password(), dto.registration(), LocalDate.now());
        Manager savedManager = managerRepository.save(manager);
        return UserResponseDTO.fromEntity(savedManager);
    }

    @Transactional
    public void removeManager(Long managerId){
        Manager manager = managerRepository.findById(managerId)
                        .orElseThrow(() -> new br.com.ufca.sixsevenpayapi.common.exception.NotFoundException("Gerente não encontrado"));
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

    @Transactional
    public void updateSavingsInterestRate(BigDecimal newRate){
        if(newRate.compareTo(BigDecimal.ZERO) < 0){
            throw  new br.com.ufca.sixsevenpayapi.common.exception.BadRequestException("A taxa de juros não pode ser negativa");
        }
        SystemConfig config = systemConfigRepository.findAll().stream().findFirst()
                .orElse(new SystemConfig(BigDecimal.ZERO));

        config.setSavingsInterestRate(newRate);
        systemConfigRepository.save(config);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getManagerById(Long managerId){
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new br.com.ufca.sixsevenpayapi.common.exception.NotFoundException("Gerente não encontrado"));
        return UserResponseDTO.fromEntity(manager);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllManagers(){
        return managerRepository.findAll().stream().map(UserResponseDTO::fromEntity).toList();
    }

    @Transactional
    public UserResponseDTO updateManager(Long managerId, UpdateManagerDTO dto){
        Manager manager = managerRepository.findById(managerId)
                .orElseThrow(() -> new br.com.ufca.sixsevenpayapi.common.exception.NotFoundException("Gerente não encontrado"));

        if(dto.name() != null && !dto.name().isBlank()){
            manager.changeFullName(dto.name());
        }

        if(dto.email() != null && !dto.email().isBlank()){
            String cleanEmail = validateAndSanitizeEmail(dto.email());
            if(userRepository.existsByEmail(cleanEmail) && !cleanEmail.equals(manager.getEmail())){
                throw new br.com.ufca.sixsevenpayapi.common.exception.ConflictException("Email já cadastrado");
            }
            manager.changeEmail(cleanEmail);
        }

        if(dto.phone() != null && !dto.phone().isBlank()){
            String cleanPhone = validateAndSanitizePhone(dto.phone());
            manager.changePhone(cleanPhone);
        }

        Manager saved = managerRepository.save(manager);
        return UserResponseDTO.fromEntity(saved);
    }

}
