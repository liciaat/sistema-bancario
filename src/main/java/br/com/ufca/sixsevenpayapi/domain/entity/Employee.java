package br.com.ufca.sixsevenpayapi.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Employee extends User {

    @Column(nullable = false, unique = true, length = 20)
    private String registration;

    @Column(nullable = false)
    private LocalDate hireDate;

    protected Employee() {
        super();
    }

    protected Employee(
            String fullName,
            String cpf,
            String email,
            String password,
            String phone,
            String registration,
            LocalDate hireDate) {

        super(fullName, cpf, email, password, phone);

        this.registration = registration;
        this.hireDate = hireDate;
    }

    public String getRegistration() {
        return registration;
    }

    public void changeRegistration(String registration) {
        this.registration = registration;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void changeHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }
}
