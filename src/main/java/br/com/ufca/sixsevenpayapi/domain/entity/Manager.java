package br.com.ufca.sixsevenpayapi.domain.entity;

import br.com.ufca.sixsevenpayapi.common.exception.BadRequestException;
import br.com.ufca.sixsevenpayapi.domain.enums.RequestStatus;
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

    public void approveRequest(Request request) {
        validateRequestDecision(request);
        request.setStatus(RequestStatus.APPROVED);
    }

    public void rejectRequest(Request request) {
        validateRequestDecision(request);
        request.setStatus(RequestStatus.REJECTED);
    }

    private void validateRequestDecision(Request request) {
        if (!isActive()) {
            throw new BadRequestException("Gerente inativo");
        }
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new BadRequestException("Apenas solicitações pendentes podem ser processadas");
        }
    }
}
