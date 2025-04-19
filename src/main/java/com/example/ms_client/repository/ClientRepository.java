package com.example.ms_client.repository;

import com.example.ms_client.entity.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

    // Кастомный метод для поиска с использованием ILIKE
    @Query("""
    SELECT c FROM Client c 
    WHERE LOWER(c.fullName) LIKE LOWER(CONCAT(:querySymbol, '%')) 
       OR LOWER(c.shortName) LIKE LOWER(CONCAT(:querySymbol, '%'))
    """)
    List<Client> searchClientsByPrefix(@Param("querySymbol") String querySymbol, Pageable pageable);

}

