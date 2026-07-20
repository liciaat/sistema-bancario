package br.com.ufca.sixsevenpayapi.application.service;

import br.com.ufca.sixsevenpayapi.domain.entity.Account;
import br.com.ufca.sixsevenpayapi.domain.entity.Customer;
import br.com.ufca.sixsevenpayapi.domain.entity.User;
import br.com.ufca.sixsevenpayapi.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import br.com.ufca.sixsevenpayapi.domain.utils.CpfValidator;

import java.math.BigDecimal;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String cpf, String password){
        String clearCpf = CpfValidator.validateAndSanitizeCpf(cpf);
        User user = userRepository.findByCpf(clearCpf);
        if(user == null){
            throw new RuntimeException("Usuário não existe");
        }
        if(!password.equals(user.getPassword())){
            throw new RuntimeException("Senha incorreta");
        }
        return user;
    }

    @Transactional
    public void updatePassword(String cpf,String password, String newPassword){
        String clearCpf = CpfValidator.validateAndSanitizeCpf(cpf);
        User user = userRepository.findByCpf(clearCpf);
        if(user == null){
            throw new RuntimeException("Usuário não existe");
        }
        if(!password.equals(user.getPassword())){
            throw new RuntimeException("Senha incorreta");
        }
        if(password.equals(newPassword)){
            throw new RuntimeException("Senha igual a anterior");
        }
        user.changePassword(newPassword);
    }

    @Transactional
    public void deleteOwnAccount(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        if (user instanceof Customer customer) {
            if (customer.getAccounts() != null && !customer.getAccounts().isEmpty()) {
                for (Account account : customer.getAccounts()) {
                    if (account.getBalance() != null && account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
                        throw new RuntimeException("Não é possível excluir o perfil: ainda existe saldo na conta " + account.getAccountNumber());
                    }
                }
            }
        }
        userRepository.deleteById(id);

    }

}
