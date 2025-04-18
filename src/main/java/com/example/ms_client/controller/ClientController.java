package com.example.ms_client.controller;

import com.example.ms_client.dto.ClientDTO;
import com.example.ms_client.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/client")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @Operation(summary = "Создать нового клиента")
    @ApiResponse(responseCode = "201", description = "Клиент успешно создан")
    @PostMapping
    public ResponseEntity<ClientDTO> createClient(@RequestBody @Valid ClientDTO dto) {
        ClientDTO saved = clientService.createClient(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @Operation(summary = "Обновить клиента по ID")
    @ApiResponse(responseCode = "200", description = "Клиент успешно обновлён")
    @ApiResponse(responseCode = "404", description = "Клиент не найден")
    @PutMapping("/{id}")
    public ResponseEntity<ClientDTO> updateClient(@PathVariable UUID id, @RequestBody @Valid ClientDTO dto) {
        return ResponseEntity.ok(clientService.updateClient(id, dto));
    }

    @Operation(summary = "Получить клиента по ID")
    @ApiResponse(responseCode = "200", description = "Клиент найден")
    @ApiResponse(responseCode = "404", description = "Клиент не найден")
    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable UUID id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @GetMapping
    public ResponseEntity<List<ClientDTO>> getAllClients(
            @RequestParam(required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        return ResponseEntity.ok(clientService.getClients(active, PageRequest.of(page, size)));
    }


    @Operation(summary = "Поиск клиентов по активности с пагинацией")
    @ApiResponse(responseCode = "200", description = "Клиенты успешно найдены")
    @GetMapping("/search-by-active")
    public ResponseEntity<List<ClientDTO>> searchByActive(
            @RequestParam boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(clientService.getClientsByActiveStatus(active, PageRequest.of(page, size)));
    }

    @Operation(summary = "Поиск клиентов по querySymbol в fullName или shortName")
    @ApiResponse(responseCode = "200", description = "Клиенты успешно найдены")
    @GetMapping("/search")
    public ResponseEntity<List<ClientDTO>> searchClients(
            @RequestParam("querySymbol") String querySymbol,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        List<ClientDTO> results = clientService.searchClientsByQuerySymbol(querySymbol, pageable);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "Удалить клиента по ID")
    @ApiResponse(responseCode = "204", description = "Клиент успешно удалён")
    @ApiResponse(responseCode = "404", description = "Клиент не найден")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable UUID id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Удалить всех клиентов")
    @ApiResponse(responseCode = "204", description = "Все клиенты удалены")
    @DeleteMapping
    public ResponseEntity<Void> deleteAllClients() {
        clientService.deleteAllClients();
        return ResponseEntity.noContent().build();
    }
}
