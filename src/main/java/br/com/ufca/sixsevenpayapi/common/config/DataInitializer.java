package br.com.ufca.sixsevenpayapi.common.config;

import br.com.ufca.sixsevenpayapi.domain.entity.Administrator;
import br.com.ufca.sixsevenpayapi.repository.AdministratorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(AdministratorRepository administratorRepository) {
        return args -> {
            if(administratorRepository.count() == 0) {
                Administrator administrator = new Administrator("Administrador Geral", "00000000000", "admin@gmail.com", "admin", "0000000000", "ADM-001", LocalDate.now());
                administratorRepository.save(administrator);
                System.out.println("====== Conta do administrador criada com sucesso ======");
            }
        };
    }

}
