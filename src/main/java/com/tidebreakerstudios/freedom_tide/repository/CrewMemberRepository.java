package com.tidebreakerstudios.freedom_tide.repository;

import com.tidebreakerstudios.freedom_tide.model.CrewMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositório Spring Data JPA para a entidade CrewMember.
 */
@Repository
public interface CrewMemberRepository extends JpaRepository<CrewMember, Long> {
}
