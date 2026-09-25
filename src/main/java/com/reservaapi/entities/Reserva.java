package com.reservaapi.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.*;

@Entity
@Table(name = "reserva")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reserva {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "codigo")
  private Long codigo;

  @Column(name = "data_inicial", nullable = false)
  private LocalDate dataInicial;

  @Column(name = "data_final", nullable = false)
  private LocalDate dataFinal;

  @Column(name = "hora_inicial", nullable = false)
  private LocalTime horaInicial;

  @Column(name = "hora_final", nullable = false)
  private LocalTime horaFinal;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "usuario_codigo", nullable = false)
  private Usuario usuario;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "recurso_codigo", nullable = false)
  private Recurso recurso;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "status_reserva_codigo", nullable = false)
  private StatusReserva statusReserva;

  @Version private Long versao;
}
