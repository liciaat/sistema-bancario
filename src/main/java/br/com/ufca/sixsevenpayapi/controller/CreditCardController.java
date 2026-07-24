package br.com.ufca.sixsevenpayapi.controller;

import br.com.ufca.sixsevenpayapi.application.dto.*;
import br.com.ufca.sixsevenpayapi.application.service.CreditCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credit-cards")
@Tag(
        name = "Cartão de crédito",
        description = "Operações de compras no cŕedito, pagamento de faturas e consultas"
)
public class CreditCardController {

    private final CreditCardService creditCardService;
    public CreditCardController(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }

    @Operation(
            summary = "Processar uma compra",
            description = "Realiza uma nova compra no cartão de crédito"
    )
    @PostMapping("/purchase")
    public ResponseEntity<PurchaseResponseDTO> processPurchase(@Valid @RequestBody PurchaseDTO dto){
        PurchaseResponseDTO response = creditCardService.processPurchase(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Consultar fatura específica",
            description = "Retorna os detalhes de uma fatura através do seu ID."
    )
    @GetMapping("/invoices/{invoiceId}")
    public ResponseEntity<InvoiceResponseDTO> getInvoice(@PathVariable Long invoiceId){
        InvoiceResponseDTO response = creditCardService.getInvoice(invoiceId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Pagar fatura",
            description = "Debita o valor da fatura do saldo da conta vinculada e libera o limite do cartão."
    )
    @PatchMapping("/invoices/invoice/pay")
    public ResponseEntity<InvoiceResponseDTO> payInvoice(@Valid @RequestBody PayInvoiceDTO dto){
        InvoiceResponseDTO response = creditCardService.payInvoice(dto);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Consultar cartão do cliente",
            description = "Retorna os dados do cartão de crédito associado a um cliente específico."
    )
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<CreditCardResponseDTO> getCreditCardByCustomer(@PathVariable Long customerId){
        CreditCardResponseDTO response = creditCardService.getCreditCardByCustomer(customerId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Consultar faturas do cliente",
            description = "Lista todas as faturas (pagas e pendentes) de um cliente."
    )
    @GetMapping("/customer/{customerId}/invoices")
    public ResponseEntity<List<InvoiceResponseDTO>> getCustomerInvoices(@PathVariable Long customerId){
        List<InvoiceResponseDTO> response = creditCardService.getCustomerInvoices(customerId);
        return ResponseEntity.ok(response);
    }

}
