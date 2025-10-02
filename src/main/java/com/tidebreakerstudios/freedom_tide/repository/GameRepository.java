package com.tidebreakerstudios.freedom_tide.repository;

import com.tidebreakerstudios.freedom_tide.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório Spring Data JPA para a entidade Game.
 * Fornece métodos CRUD prontos para uso e a base para consultas customizadas.
 */
@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
}
