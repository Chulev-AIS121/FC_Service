package com.example.ms_client.controller;

import com.example.ms_client.dto.ClientDTO;
import com.example.ms_client.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.domain.PageRequest;
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
    @ApiResponse(responseCode = "200", description = "Клиент успешно создан")
    @PostMapping
    public ClientDTO createClient(@RequestBody ClientDTO dto) {
        return clientService.createClient(dto);
    }

    @Operation(summary = "Обновить клиента по ID")
    @ApiResponse(responseCode = "200", description = "Клиент успешно обновлён")
    @ApiResponse(responseCode = "404", description = "Клиент не найден")
    @PutMapping("/{id}")
    public ClientDTO updateClient(@PathVariable UUID id, @RequestBody ClientDTO dto) {
        return clientService.updateClient(id, dto);
    }

    @Operation(summary = "Получить клиента по ID")
    @ApiResponse(responseCode = "200", description = "Клиент найден")
    @ApiResponse(responseCode = "404", description = "Клиент не найден")
    @GetMapping("/{id}")
    public ClientDTO getClientById(@PathVariable UUID id) {
        return clientService.getClientById(id);
    }

    @Operation(summary = "Получить список всех клиентов с пагинацией")
    @ApiResponse(responseCode = "200", description = "Клиенты успешно получены")
    @GetMapping
    public List<ClientDTO> getAllClients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return clientService.getAllClients(PageRequest.of(page, size));
    }

    @Operation(summary = "Поиск клиентов по активности с пагинацией")
    @ApiResponse(responseCode = "200", description = "Клиенты успешно найдены")
    @GetMapping("/search")
    public List<ClientDTO> searchByActive(
            @RequestParam boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return clientService.getClientsByActiveStatus(active, PageRequest.of(page, size));
    }
}
