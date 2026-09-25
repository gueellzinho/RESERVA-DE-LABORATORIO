package com.reservaapi.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.*;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "codigo")
  private Long codigo;

  @Column(name = "cpf", nullable = false, unique = true, length = 11)
  private String cpf;

  @Column(name = "nome_completo", nullable = false, length = 80)
  private String nomeCompleto;

  @Column(name = "data_aniversario", nullable = false)
  private LocalDate dataAniversario;

  @Column(name = "celular", nullable = false, length = 20)
  private String celular;

  @Column(name = "email", nullable = false, unique = true, length = 80)
  private String email;

  @Column(name = "login", nullable = false, unique = true, length = 80)
  private String login;

  @Column(name = "senha", nullable = false, length = 255)
  private String senha;

  @Version private Long versao;
}
