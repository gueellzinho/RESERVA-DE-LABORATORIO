package com.reservaapi.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "recurso")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(
    name = "tipo_recurso",
    discriminatorType = DiscriminatorType.STRING,
    length = 20)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Recurso {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "codigo")
  private Long codigo;

  @Column(name = "nome", nullable = false, length = 80)
  private String nome;

  @Column(name = "capacidade", nullable = false)
  private Integer capacidade;

  @Column(name = "localizacao", nullable = false, length = 50)
  private String localizacao;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "status_recurso_codigo", nullable = false)
  private StatusRecurso statusRecurso;

  @Version private Long versao;
}
