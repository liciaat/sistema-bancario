package br.com.ufca.sixsevenpayapi.controller;

import br.com.ufca.sixsevenpayapi.application.dto.ProcessRequestDTO;
import br.com.ufca.sixsevenpayapi.application.dto.CreditRequestDTO;
import br.com.ufca.sixsevenpayapi.application.dto.RequestResponseDTO;
import br.com.ufca.sixsevenpayapi.application.dto.SavingsAccountRequestDTO;
import br.com.ufca.sixsevenpayapi.application.service.RequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@Tag(
        name = "Solicitações",
        description = "Gerenciamento de solicitações de contas, crédito e encerramento"
)
public class RequestController {

    private final RequestService requestService;
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @Operation(
            summary = "Solicitar conta poupança",
            description = "Cria uma solicitação pendente para abertura de conta poupança para um cliente existente"
    )
    @PostMapping("/savings")
    public ResponseEntity<RequestResponseDTO> requestSavingsAccount(@Valid @RequestBody SavingsAccountRequestDTO dto){
        RequestResponseDTO response =  requestService.requestSavingsAccount(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Solicitar crédito",
            description = "Cria uma solicitação pendente para aquisição de um cartão de crédito ou aumento de limite."
    )
    @PostMapping("/credit")
    public ResponseEntity<RequestResponseDTO> requestCredit(@Valid @RequestBody CreditRequestDTO dto){
        RequestResponseDTO response =  requestService.requestCredit(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @Operation(
            summary = "Aprovar solicitação",
            description = "Aprova uma solicitação pendente (conta, crédito ou encerramento) mediante validação das credenciais do gerente."
    )
    @PatchMapping("/request/approve")
    public ResponseEntity<RequestResponseDTO> requestApproved(@Valid @RequestBody ProcessRequestDTO dto) {
         RequestResponseDTO response = requestService.approveRequest(dto);
         return ResponseEntity.ok().body(response);
    }

    @Operation(
            summary = "Rejeitar solicitação",
            description = "Nega uma solicitação pendente mediante validação das credenciais do gerente."
    )
    @PatchMapping("/request/reject")
    public ResponseEntity<RequestResponseDTO> requestReject(@Valid @RequestBody ProcessRequestDTO dto) {
        RequestResponseDTO response = requestService.rejectRequest(dto);
        return ResponseEntity.ok().body(response);
    }

    @Operation(
            summary = "Listar solicitações do cliente",
            description = "Retorna o histórico de todas as solicitações (aprovadas, pendentes ou rejeitadas) feitas por um cliente específico."
    )
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<RequestResponseDTO>> getRequestsByCustomer(@PathVariable Long customerId) {
        List<RequestResponseDTO> response = requestService.getRequestsByCustomer(customerId);
        return ResponseEntity.ok().body(response);
    }


}
