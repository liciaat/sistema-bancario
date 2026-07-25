package br.com.ufca.sixsevenpayapi.application.service;

import br.com.ufca.sixsevenpayapi.application.dto.UpdateCustomerDTO;
import br.com.ufca.sixsevenpayapi.application.dto.UserResponseDTO;
import br.com.ufca.sixsevenpayapi.application.dto.AccountResponseDTO;
import br.com.ufca.sixsevenpayapi.domain.entity.Customer;
import br.com.ufca.sixsevenpayapi.domain.entity.Account;
import br.com.ufca.sixsevenpayapi.domain.entity.User;
import br.com.ufca.sixsevenpayapi.domain.utils.EmailValidator;
import br.com.ufca.sixsevenpayapi.domain.utils.PhoneValidator;
import br.com.ufca.sixsevenpayapi.domain.utils.CpfValidator;
import br.com.ufca.sixsevenpayapi.repository.CustomerRepository;
import br.com.ufca.sixsevenpayapi.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.ArrayList;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    public CustomerService(CustomerRepository customerRepository, UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getCustomer(Long customerId){
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new br.com.ufca.sixsevenpayapi.common.exception.NotFoundException("Cliente não encontrado"));
        return UserResponseDTO.fromEntity(customer);
    }

    @Transactional
    public UserResponseDTO updateCustomer(Long customerId, UpdateCustomerDTO dto){
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new br.com.ufca.sixsevenpayapi.common.exception.NotFoundException("Cliente não encontrado"));

        if (dto.name() != null && !dto.name().isBlank()){
            customer.changeFullName(dto.name());
        }

        if (dto.email() != null && !dto.email().isBlank()){
            String cleanEmail = EmailValidator.validateAndSanitizeEmail(dto.email());
            if (!cleanEmail.equals(customer.getEmail()) && userRepository.existsByEmail(cleanEmail)){
                throw new br.com.ufca.sixsevenpayapi.common.exception.ConflictException("Email já cadastrado");
            }
            customer.changeEmail(cleanEmail);
        }

        if (dto.phone() != null && !dto.phone().isBlank()){
            String cleanPhone = PhoneValidator.validateAndSanitizePhone(dto.phone());
            if (!cleanPhone.equals(customer.getPhone()) && userRepository.existsByPhone(cleanPhone)){
                throw new br.com.ufca.sixsevenpayapi.common.exception.ConflictException("Telefone já cadastrado");
            }
            customer.changePhone(cleanPhone);
        }

        Customer saved = customerRepository.save(customer);
        return UserResponseDTO.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<AccountResponseDTO> getAccountsByCustomerId(Long customerId){
        Customer c = customerRepository.findById(customerId)
                .orElseThrow(() -> new br.com.ufca.sixsevenpayapi.common.exception.NotFoundException("Cliente não encontrado"));
        List<AccountResponseDTO> response = new ArrayList<>();
        if(c.getAccounts() != null){
            for(Account a : c.getAccounts()){
                response.add(AccountResponseDTO.fromEntity(a));
            }
        }
        return response;
    }

    @Transactional(readOnly = true)
    public List<AccountResponseDTO> getAccountsByIdentifier(String customer){
        List<AccountResponseDTO> response = new ArrayList<>();
        try{
            Long customerId = Long.parseLong(customer);
            return getAccountsByCustomerId(customerId);
        }catch(NumberFormatException ex){
            String cleanCpf = CpfValidator.validateAndSanitizeCpf(customer);
            User user = userRepository.findByCpf(cleanCpf);
            if(user == null || !(user instanceof Customer)){
                throw new br.com.ufca.sixsevenpayapi.common.exception.NotFoundException("Cliente não encontrado");
            }
            Customer c = (Customer) user;
            if(c.getAccounts() != null){
                for(Account a : c.getAccounts()){
                    response.add(AccountResponseDTO.fromEntity(a));
                }
            }
            return response;
        }
    }
}
