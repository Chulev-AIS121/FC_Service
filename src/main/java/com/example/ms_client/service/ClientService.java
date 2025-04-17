package com.example.ms_client.service;

import com.example.ms_client.dto.ClientDTO;
import com.example.ms_client.entity.Client;
import com.example.ms_client.exception.ClientNotFoundException;
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
        Client client = mapper.toEntity(dto); // создаем entity из dto
        client.setCreateDateTime(LocalDateTime.now()); // устанавливаем дату создания
        client.setUpdateDateTime(LocalDateTime.now()); // устанавливаем дату обновления
        return mapper.toDto(repository.save(client)); // сохраняем entity и возвращаем dto
    }


    @Transactional
    public ClientDTO updateClient(UUID id, ClientDTO dto) {
        return repository.findById(id)
                .map(existing -> {
                    mapper.updateClientFromDto(dto, existing);
                    existing.setUpdateDateTime(LocalDateTime.now());
                    return mapper.toDto(repository.save(existing));
                })
                .orElseThrow(() -> new ClientNotFoundException(id));
    }

    public ClientDTO getClientById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ClientNotFoundException(id));
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

    @Transactional
    public void deleteClient(UUID id) {
        if (!repository.existsById(id)) {
            throw new ClientNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
