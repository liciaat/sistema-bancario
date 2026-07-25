package br.com.ufca.sixsevenpayapi.controller;

import br.com.ufca.sixsevenpayapi.application.dto.UpdateCustomerDTO;
import br.com.ufca.sixsevenpayapi.application.dto.UserResponseDTO;
import br.com.ufca.sixsevenpayapi.application.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import br.com.ufca.sixsevenpayapi.application.dto.StandardErrorDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.com.ufca.sixsevenpayapi.application.dto.AccountResponseDTO;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Clientes", description = "Operações básicas de perfil do cliente")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @Operation(summary = "Obter dados do cliente", description = "Retorna informações básicas do cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Dados retornados"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class)))
    })
    @GetMapping("/{customerId}")
    public ResponseEntity<UserResponseDTO> getCustomer(@PathVariable Long customerId){
        UserResponseDTO response = customerService.getCustomer(customerId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualizar dados flexíveis do cliente", description = "Atualiza nome, email e/ou telefone. Campos nulos serão ignorados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Atualização realizada"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class))),
            @ApiResponse(responseCode = "409", description = "Conflito: email/telefone já cadastrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class)))
    })
    @PatchMapping("/{customerId}")
    public ResponseEntity<UserResponseDTO> updateCustomer(@PathVariable Long customerId, @Valid @RequestBody UpdateCustomerDTO dto){
        UserResponseDTO response = customerService.updateCustomer(customerId, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar contas do cliente (por id)", description = "Retorna as contas vinculadas a um cliente pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contas retornadas"),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class)))
    })
    @GetMapping("/{customerId}/accounts")
    public ResponseEntity<List<AccountResponseDTO>> getAccountsByCustomerId(@PathVariable Long customerId){
        List<AccountResponseDTO> response = customerService.getAccountsByCustomerId(customerId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar contas do cliente (id ou CPF)", description = "Query param 'customer' pode ser o id numérico ou o CPF do cliente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contas retornadas"),
            @ApiResponse(responseCode = "400", description = "Requisição inválida", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Cliente não encontrado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno", content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandardErrorDTO.class)))
    })
    @GetMapping("/accounts")
    public ResponseEntity<List<AccountResponseDTO>> getAccountsByIdentifier(@RequestParam("customer") String customer){
        List<AccountResponseDTO> response = customerService.getAccountsByIdentifier(customer);
        return ResponseEntity.ok(response);
    }
}
