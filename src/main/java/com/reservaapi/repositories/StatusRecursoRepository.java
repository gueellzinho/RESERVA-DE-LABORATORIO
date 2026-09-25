package com.reservaapi.repositories;

import com.reservaapi.entities.StatusRecurso;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusRecursoRepository extends JpaRepository<StatusRecurso, Long> {

  Optional<StatusRecurso> findByNomeIgnoreCase(String nome);
}
