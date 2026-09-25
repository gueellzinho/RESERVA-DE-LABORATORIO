package com.reservaapi.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "status_reserva")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusReserva {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "codigo")
  private Long codigo;

  @Column(name = "nome", nullable = false, unique = true, length = 20)
  private String nome;

  @Version private Long versao;
}
