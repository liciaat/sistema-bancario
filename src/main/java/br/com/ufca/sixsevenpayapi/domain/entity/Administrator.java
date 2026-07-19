package br.com.ufca.sixsevenpayapi.domain.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

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
            LocalDate hireDate) {

        super(fullName, cpf, email, password, phone, registration, hireDate);
    }

}
