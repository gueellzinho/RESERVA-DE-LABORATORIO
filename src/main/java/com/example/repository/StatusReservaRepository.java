package com.reservaapi.repositories;

import com.reservaapi.entities.StatusReserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatusReservaRepository extends JpaRepository<StatusReserva, Long> {

    Optional<StatusReserva> findByNomeIgnoreCase(String nome);
}
