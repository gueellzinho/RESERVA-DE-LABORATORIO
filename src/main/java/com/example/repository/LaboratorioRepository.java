package com.reservaapi.repositories;

import com.reservaapi.entities.Laboratorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LaboratorioRepository extends JpaRepository<Laboratorio, Long>, JpaSpecificationExecutor<Laboratorio> {
}
