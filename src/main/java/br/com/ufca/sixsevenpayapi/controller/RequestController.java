package br.com.ufca.sixsevenpayapi.controller;

import br.com.ufca.sixsevenpayapi.application.dto.CreditRequestDTO;
import br.com.ufca.sixsevenpayapi.application.dto.RequestResponseDTO;
import br.com.ufca.sixsevenpayapi.application.dto.SavingsAccountRequestDTO;
import br.com.ufca.sixsevenpayapi.application.service.RequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private RequestService requestService;
    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping("/savings")
    public ResponseEntity<RequestResponseDTO> requestSavingsAccount(@Valid @RequestBody SavingsAccountRequestDTO dto){
        RequestResponseDTO response =  requestService.requestSavingsAccount(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PostMapping("/credit")
    public ResponseEntity<RequestResponseDTO> requestCredit(@Valid @RequestBody CreditRequestDTO dto){
        RequestResponseDTO response =  requestService.requestCredit(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }

    @PatchMapping("/{requestId}/approve")
    public ResponseEntity<RequestResponseDTO> requestApprove(@PathVariable Long requestId, @RequestParam Long managerId) {
         RequestResponseDTO response = requestService.approveRequest(requestId, managerId);
         return ResponseEntity.ok().body(response);
    }

    @PatchMapping("/{requestId}/reject")
    public ResponseEntity<RequestResponseDTO> requestReject(@PathVariable Long requestId, @RequestParam Long managerId) {
        RequestResponseDTO response = requestService.rejectRequest(requestId, managerId);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<RequestResponseDTO>> getRequestsByCustomer(@PathVariable Long customerId) {
        List<RequestResponseDTO> response = requestService.getRequestsByCustomer(customerId);
        return ResponseEntity.ok().body(response);
    }


}
