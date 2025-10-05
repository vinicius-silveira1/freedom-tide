package com.tidebreakerstudios.freedom_tide.repository;

import com.tidebreakerstudios.freedom_tide.model.Contract;
import com.tidebreakerstudios.freedom_tide.model.ContractStatus;
import com.tidebreakerstudios.freedom_tide.model.Port;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByStatus(ContractStatus status);
    Optional<Contract> findByTitle(String title);

    List<Contract> findByStatusAndOriginPortAndRequiredReputationLessThanEqualAndRequiredInfamyLessThanEqualAndRequiredAllianceLessThanEqual(
            ContractStatus status,
            Port originPort,
            Integer reputation,
            Integer infamy,
            Integer alliance
    );
}
