package com.reservaapi.repositories;

import com.reservaapi.entities.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Repositorio do tipo base Recurso. Usado para as consultas que precisam
 * enxergar Laboratorio e Sala de forma unificada (ex: buscar um recurso por
 * codigo ao criar uma Reserva, sem precisar saber se e Laboratorio ou Sala).
 *
 * JpaSpecificationExecutor permite montar a consulta combinando dinamicamente
 * os filtros de nome, capacidade, localizacao e status pedidos no PDF
 * (implementado no Passo 6, na camada de service/controller).
 */
public interface RecursoRepository extends JpaRepository<Recurso, Long>, JpaSpecificationExecutor<Recurso> {
}
