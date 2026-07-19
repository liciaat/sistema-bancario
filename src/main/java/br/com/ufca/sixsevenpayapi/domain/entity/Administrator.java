package br.com.ufca.sixsevenpayapi.domain.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "administrators")
public class Administrator extends Employee {

    protected Administrator() {
        super();
    }

    public Administrator(
            String fullName,
            String cpf,
            String email,
            String password,
            String phone,
            String registration,
            java.time.LocalDate hireDate) {

        super(fullName, cpf, email, password, phone, registration, hireDate);
    }

}
