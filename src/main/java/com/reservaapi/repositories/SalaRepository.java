package com.reservaapi.repositories;

import com.reservaapi.entities.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SalaRepository extends JpaRepository<Sala, Long>, JpaSpecificationExecutor<Sala> {}
