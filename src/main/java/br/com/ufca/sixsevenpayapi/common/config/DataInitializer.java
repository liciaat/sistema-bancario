package br.com.ufca.sixsevenpayapi.common.config;

import br.com.ufca.sixsevenpayapi.domain.entity.Administrator;
import br.com.ufca.sixsevenpayapi.domain.entity.SystemConfig;
import br.com.ufca.sixsevenpayapi.repository.AdministratorRepository;
import br.com.ufca.sixsevenpayapi.repository.SystemConfigRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(AdministratorRepository administratorRepository, SystemConfigRepository systemConfigRepository) {
        return args -> {
            if(administratorRepository.count() == 0) {
                Administrator administrator = new Administrator("Administrador Geral", "00000000000", "admin@gmail.com", "admin", "0000000000", "ADM-001", LocalDate.now());
                administratorRepository.save(administrator);
                System.out.println("====== Conta do administrador criada com sucesso ======");
            }
            if(systemConfigRepository.count() == 0) {
                systemConfigRepository.save(new SystemConfig(new BigDecimal("0.05")));
            }
        };
    }

}
