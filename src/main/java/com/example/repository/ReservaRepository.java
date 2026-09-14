package com.reservaapi.repositories;

import com.reservaapi.entities.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long>, JpaSpecificationExecutor<Reserva> {

    List<Reserva> findByStatusReserva_NomeIgnoreCase(String nomeStatus);

    /**
     * Busca reservas ATIVAS do mesmo recurso, na mesma data, cujo intervalo
     * de horario se sobrepoe ao intervalo informado. Usada no Passo 5 para
     * impedir reservas conflitantes (duas reservas no mesmo horario para o
     * mesmo laboratorio/sala) e para calcular o status OCUPADO do recurso.
     *
     * Duas faixas [inicioA, fimA) e [inicioB, fimB) se sobrepoem quando:
     * inicioA < fimB E inicioB < fimA
     */
    @Query("""
            SELECT r FROM Reserva r
            WHERE r.recurso.codigo = :recursoCodigo
              AND r.dataInicial = :data
              AND UPPER(r.statusReserva.nome) = 'ATIVA'
              AND r.horaInicial < :horaFinal
              AND :horaInicial < r.horaFinal
            """)
    List<Reserva> buscarReservasConflitantes(
            @Param("recursoCodigo") Long recursoCodigo,
            @Param("data") LocalDate data,
            @Param("horaInicial") LocalTime horaInicial,
            @Param("horaFinal") LocalTime horaFinal
    );

    /**
     * Reservas ATIVAS cujo horario final ja passou (usadas pela rotina que
     * marca reservas como CONCLUIDA 1 minuto apos o horario final).
     */
    @Query("""
            SELECT r FROM Reserva r
            WHERE UPPER(r.statusReserva.nome) = 'ATIVA'
              AND (r.dataFinal < :dataReferencia
                   OR (r.dataFinal = :dataReferencia AND r.horaFinal <= :horaReferencia))
            """)
    List<Reserva> buscarReservasAtivasVencidas(
            @Param("dataReferencia") LocalDate dataReferencia,
            @Param("horaReferencia") LocalTime horaReferencia
    );
}
