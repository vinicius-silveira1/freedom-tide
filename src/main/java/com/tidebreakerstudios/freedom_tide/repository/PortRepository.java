package com.tidebreakerstudios.freedom_tide.repository;

import com.tidebreakerstudios.freedom_tide.model.Port;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortRepository extends JpaRepository<Port, Long> {
    /**
     * Encontra um porto pelo seu nome único.
     * @param name O nome do porto a ser buscado.
     * @return Um Optional contendo o porto se encontrado.
     */
    Optional<Port> findByName(String name);
}
