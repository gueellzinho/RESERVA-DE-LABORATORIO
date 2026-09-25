package com.reservaapi.repositories;

import com.reservaapi.entities.Recurso;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface RecursoRepository
    extends JpaRepository<Recurso, Long>, JpaSpecificationExecutor<Recurso> {
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select r from Recurso r where r.codigo=:codigo")
  Optional<Recurso> findLocked(@Param("codigo") Long codigo);
}
