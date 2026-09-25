package com.reservaapi.repositories;

import com.reservaapi.entities.Reserva;
import java.time.*;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ReservaRepository
    extends JpaRepository<Reserva, Long>, JpaSpecificationExecutor<Reserva> {
  @Query(
      """
      select r from Reserva r where r.recurso.codigo=:recurso and r.dataInicial=:data
      and upper(r.statusReserva.nome)='ATIVA' and r.horaInicial < :fim and r.horaFinal > :inicio
      and (:ignorar is null or r.codigo <> :ignorar)
      """)
  List<Reserva> conflitos(
      @Param("recurso") Long recurso,
      @Param("data") LocalDate data,
      @Param("inicio") LocalTime inicio,
      @Param("fim") LocalTime fim,
      @Param("ignorar") Long ignorar);

  @Query(
      """
      select r from Reserva r where upper(r.statusReserva.nome)='ATIVA'
      and (r.dataFinal < :data or (r.dataFinal=:data and r.horaFinal <= :hora))
      """)
  List<Reserva> vencidas(@Param("data") LocalDate data, @Param("hora") LocalTime hora);

  boolean existsByRecurso_CodigoAndStatusReserva_NomeIgnoreCase(Long codigo, String nome);

  @Query(
      """
      select count(r)>0 from Reserva r where r.recurso.codigo=:recurso and r.dataInicial=:data
      and upper(r.statusReserva.nome)='ATIVA' and r.horaInicial <= :hora and r.horaFinal > :hora
      """)
  boolean ocupado(
      @Param("recurso") Long recurso, @Param("data") LocalDate data, @Param("hora") LocalTime hora);
}
