package com.reservaapi.repositories;

import com.reservaapi.entities.StatusRecurso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatusRecursoRepository extends JpaRepository<StatusRecurso, Long> {

    Optional<StatusRecurso> findByNomeIgnoreCase(String nome);
}
