package br.com.ufca.sixsevenpayapi.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "managers")
public class Manager extends Employee {

    public Manager() {
    }

    public Manager(String fullName,
                   String cpf,
                   String email,
                   String phone,
                   String password,
                   String registration,
                   LocalDate hireDate) {
        super(fullName, cpf, email, password, phone, registration, hireDate);
    }
}
