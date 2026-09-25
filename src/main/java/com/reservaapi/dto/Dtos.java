package com.reservaapi.dto;

import jakarta.validation.constraints.*;
import java.time.*;
import org.hibernate.validator.constraints.br.CPF;

public final class Dtos {
  private Dtos() {}

  public record UsuarioInput(
      @NotBlank(message = "Campo obrigatório")
          @Pattern(regexp = "[0-9]{11}", message = "CPF inválido")
          @CPF(message = "CPF inválido")
          String cpf,
      @NotBlank(message = "Campo obrigatório")
          @Size(min = 10, max = 80, message = "Quantidade de caracteres incorreta!")
          String nomeCompleto,
      @NotNull(message = "Campo obrigatório") @Past(message = "Data Inválida")
          LocalDate dataAniversario,
      @NotBlank(message = "Campo obrigatório")
          @Size(max = 20, message = "Quantidade de caracteres incorreta!")
          String celular,
      @NotBlank(message = "Campo obrigatório")
          @Size(min = 15, max = 80, message = "Quantidade de caracteres incorreta!")
          @Email(message = "E-mail inválido")
          String email,
      @NotBlank(message = "Campo obrigatório")
          @Pattern(
              regexp = "[A-Za-z0-9._-]{3,80}",
              message = "Login deve ter de 3 a 80 letras, números, ponto, hífen ou sublinhado")
          String login,
      @NotBlank(message = "Campo obrigatório")
          @Size(min = 6, max = 60, message = "Senha deve ter de 6 a 60 caracteres")
          String senha) {}

  public record UsuarioView(
      Long codigo,
      String cpf,
      String nomeCompleto,
      LocalDate dataAniversario,
      String celular,
      String email,
      String login) {}

  public record LoginInput(
      @NotBlank(message = "Campo obrigatório") String login,
      @NotBlank(message = "Campo obrigatório") String senha) {}

  public record LoginView(String mensagem, String autenticacao, UsuarioView usuario) {}

  public record StatusInput(
      @NotBlank(message = "Campo obrigatório")
          @Size(min = 3, max = 20, message = "Quantidade de caracteres incorreta!")
          String nome) {}

  public record StatusView(Long codigo, String nome) {}

  public record RecursoInput(
      @NotBlank(message = "Campo obrigatório")
          @Size(min = 3, max = 80, message = "Quantidade de caracteres incorreta!")
          String nome,
      @NotNull(message = "Campo obrigatório")
          @Min(value = 1, message = "Valor fora do escopo")
          @Max(value = 40, message = "Valor fora do escopo")
          Integer capacidade,
      @NotBlank(message = "Campo obrigatório")
          @Size(min = 15, max = 50, message = "Quantidade de caracteres incorreta!")
          String localizacao,
      @NotNull(message = "Campo obrigatório") Long statusRecursoCodigo) {}

  public record RecursoView(
      Long codigo,
      String tipo,
      String nome,
      Integer capacidade,
      String localizacao,
      StatusView statusRecurso) {}

  public record ReservaInput(
      @NotNull(message = "Campo obrigatório") LocalDate dataInicial,
      @NotNull(message = "Campo obrigatório") LocalDate dataFinal,
      @NotNull(message = "Campo obrigatório") LocalTime horaInicial,
      @NotNull(message = "Campo obrigatório") LocalTime horaFinal,
      @NotNull(message = "Campo obrigatório") Long usuarioCodigo,
      @NotNull(message = "Campo obrigatório") Long recursoCodigo,
      @NotNull(message = "Campo obrigatório") Long statusReservaCodigo) {}

  public record ReservaView(
      Long codigo,
      LocalDate dataInicial,
      LocalDate dataFinal,
      LocalTime horaInicial,
      LocalTime horaFinal,
      UsuarioView usuario,
      RecursoView recurso,
      StatusView statusReserva) {}
}
