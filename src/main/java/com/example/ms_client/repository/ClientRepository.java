package com.example.ms_client.repository;

import com.example.ms_client.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {

    // Поиск клиентов по части имени (fullName или shortName), игнорируя регистр
    Page<Client> findByFullNameContainingIgnoreCaseOrShortNameContainingIgnoreCase(
            String fullNamePart,
            String shortNamePart,
            Pageable pageable
    );

    // Получение всех клиентов по статусу активности (с пагинацией)
    Page<Client> findAllByActive(boolean active, Pageable pageable);
}
