package com.reservaapi.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "status_recurso")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusRecurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo")
    private Long codigo;

    @Column(name = "nome", nullable = false, length = 20)
    private String nome;
}
