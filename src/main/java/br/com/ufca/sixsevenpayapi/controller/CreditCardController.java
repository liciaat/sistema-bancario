package br.com.ufca.sixsevenpayapi.controller;

import br.com.ufca.sixsevenpayapi.application.dto.*;
import br.com.ufca.sixsevenpayapi.application.service.CreditCardService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credit-cards")
public class CreditCardController {

    private final CreditCardService creditCardService;
    public CreditCardController(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }

    @PostMapping("/purchase")
    public ResponseEntity<PurchaseResponseDTO> processPurchase(@Valid @RequestBody PurchaseDTO dto){
        PurchaseResponseDTO response = creditCardService.processPurchase(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/invoices/{invoiceId}")
    public ResponseEntity<InvoiceResponseDTO> getInvoice(@RequestParam Long invoiceId){
        InvoiceResponseDTO response = creditCardService.getInvoice(invoiceId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/invoices/invoice/pay")
    public ResponseEntity<InvoiceResponseDTO> payInvoice(@Valid @RequestBody PayInvoiceDTO dto){
        InvoiceResponseDTO response = creditCardService.payInvoice(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<CreditCardResponseDTO> getCreditCardByCustomer(@PathVariable Long customerId){
        CreditCardResponseDTO response = creditCardService.getCreditCardByCustomer(customerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/customer/{customerId}/invoices")
    public ResponseEntity<List<InvoiceResponseDTO>> getCustomerInvoices(@PathVariable Long customerId){
        List<InvoiceResponseDTO> response = creditCardService.getCustomerInvoices(customerId);
        return ResponseEntity.ok(response);
    }

}
