package com.tidebreakerstudios.freedom_tide.repository;

import com.tidebreakerstudios.freedom_tide.model.NarrativeEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositório Spring Data JPA para a entidade NarrativeEvent.
 */
@Repository
public interface NarrativeEventRepository extends JpaRepository<NarrativeEvent, Long> {
    /**
     * Busca um evento pelo seu código único, para evitar usar o ID que pode mudar entre ambientes.
     * @param eventCode O código do evento.
     * @return Um Optional contendo o evento, se encontrado.
     */
    Optional<NarrativeEvent> findByEventCode(String eventCode);
}
