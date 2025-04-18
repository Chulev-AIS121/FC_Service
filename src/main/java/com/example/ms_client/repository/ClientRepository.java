package com.example.ms_client.repository;

import com.example.ms_client.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {

    // Поиск клиентов по части fullName или shortName (без учёта регистра), используется в /client/search
    Page<Client> findByFullNameContainingIgnoreCaseOrShortNameContainingIgnoreCase(
            String fullNamePart,
            String shortNamePart,
            Pageable pageable
    );

    // Поиск всех клиентов по активности (true/false)
    Page<Client> findAllByActive(boolean active, Pageable pageable);

    // Проверка уникальности ИНН
    boolean existsByInn(String inn);
}

