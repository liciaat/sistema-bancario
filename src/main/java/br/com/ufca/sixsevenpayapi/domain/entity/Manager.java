package br.com.ufca.sixsevenpayapi.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "tb_managers")
public class Manager extends Employee {

    public Manager() {
    }

    public Manager(String fullName,
                   String cpf,
                   String email,
                   String password,
                   String phone,
                   String registration,
                   LocalDate hireDate) {
        super(fullName, cpf, email, password, phone, registration, hireDate);
    }
}
