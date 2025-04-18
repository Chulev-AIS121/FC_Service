package com.example.ms_client.service;

import com.example.ms_client.dto.ClientDTO;
import com.example.ms_client.entity.Client;
import com.example.ms_client.exception.ClientNotActiveException;
import com.example.ms_client.exception.ClientNotFoundException;
import com.example.ms_client.exception.InvalidClientDataException;
import com.example.ms_client.mapper.ClientMapper;
import com.example.ms_client.repository.ClientRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private final ClientRepository repository;
    private final ClientMapper mapper;

    public ClientService(ClientRepository repository, ClientMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public ClientDTO createClient(ClientDTO dto) {
        if (repository.existsByInn(dto.getInn())) {
            throw new InvalidClientDataException("Клиент с ИНН %s уже существует".formatted(dto.getInn()));
        }

        Client client = mapper.toEntity(dto);
        client.setId(UUID.randomUUID());
        client.setCreateDateTime(LocalDateTime.now());
        client.setUpdateDateTime(LocalDateTime.now());
        return mapper.toDto(repository.save(client));
    }

    public List<ClientDTO> getClients(Boolean active, Pageable pageable) {
        if (active == null) {
            return getAllClients(pageable);
        } else {
            return getClientsByActiveStatus(active, pageable);
        }
    }

    @Transactional
    public ClientDTO updateClient(UUID id, ClientDTO dto) {
        return repository.findById(id)
                .map(existing -> {
                    if (!existing.isActive()) {
                        throw new ClientNotActiveException(id);
                    }
                    mapper.updateClientFromDto(dto, existing);
                    existing.setUpdateDateTime(LocalDateTime.now());
                    return mapper.toDto(repository.save(existing));
                })
                .orElseThrow(() -> new ClientNotFoundException(id));
    }

    public ClientDTO getClientById(UUID id) {
        Client client = repository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));

        if (!client.isActive()) {
            throw new ClientNotActiveException(id);
        }

        return mapper.toDto(client);
    }

    public List<ClientDTO> getAllClients(Pageable pageable) {
        return repository.findAll(pageable).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ClientDTO> getClientsByActiveStatus(boolean active, Pageable pageable) {
        return repository.findAllByActive(active, pageable).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ClientDTO> searchClientsByQuerySymbol(String querySymbol, Pageable pageable) {
        return repository
                .findByFullNameContainingIgnoreCaseOrShortNameContainingIgnoreCase(querySymbol, querySymbol, pageable)
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteClient(UUID id) {
        if (!repository.existsById(id)) {
            throw new ClientNotFoundException(id);
        }
        repository.deleteById(id);
    }

    public void deleteAllClients() {
        repository.deleteAll();
    }
}
