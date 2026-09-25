package com.reservaapi.repositories;

import com.reservaapi.entities.StatusReserva;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusReservaRepository extends JpaRepository<StatusReserva, Long> {

  Optional<StatusReserva> findByNomeIgnoreCase(String nome);
}
