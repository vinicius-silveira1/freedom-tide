package com.tidebreakerstudios.freedom_tide.repository;

import com.tidebreakerstudios.freedom_tide.model.ShipUpgrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShipUpgradeRepository extends JpaRepository<ShipUpgrade, Long> {
    Optional<ShipUpgrade> findByName(String name);
}