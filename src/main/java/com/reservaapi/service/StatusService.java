package com.reservaapi.service;

import com.reservaapi.dto.Dtos.*;
import com.reservaapi.entities.*;
import com.reservaapi.error.ApiException;
import com.reservaapi.mapper.ApiMapper;
import com.reservaapi.repositories.*;
import java.text.Normalizer;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StatusService implements ApplicationRunner {
  private final StatusRecursoRepository recursos;
  private final StatusReservaRepository reservas;
  private final ApiMapper mapper;
  private static final Set<String> RESOURCE = Set.of("LIVRE", "OCUPADO", "BLOQUEADO");
  private static final Set<String> BOOKING = Set.of("ATIVA", "CANCELADA", "CONCLUIDA");

  public static String normalize(String text) {
    return Normalizer.normalize(text.trim(), Normalizer.Form.NFD)
        .replaceAll("\\p{M}", "")
        .toUpperCase(Locale.ROOT);
  }

  public StatusRecurso recurso(Long id) {
    return recursos.findById(id).orElseThrow(() -> ApiException.missing("Status do recurso"));
  }

  public StatusReserva reserva(Long id) {
    return reservas.findById(id).orElseThrow(() -> ApiException.missing("Status da reserva"));
  }

  public StatusRecurso recurso(String nome) {
    return recursos
        .findByNomeIgnoreCase(nome)
        .orElseThrow(() -> ApiException.missing("Status " + nome));
  }

  public StatusReserva reserva(String nome) {
    return reservas
        .findByNomeIgnoreCase(nome)
        .orElseThrow(() -> ApiException.missing("Status " + nome));
  }

  public List<StatusView> list(boolean resource) {
    return resource
        ? recursos.findAll().stream().map(mapper::status).toList()
        : reservas.findAll().stream().map(mapper::status).toList();
  }

  public StatusView get(boolean resource, Long id) {
    return resource ? mapper.status(recurso(id)) : mapper.status(reserva(id));
  }

  public StatusView save(boolean resource, Long id, StatusInput d) {
    String name = normalize(d.nome());
    if (name.length() < 3 || name.length() > 20)
      throw ApiException.invalid("nome", "Quantidade de caracteres incorreta!");
    if (resource) {
      var s = id == null ? new StatusRecurso() : recurso(id);
      if (id != null && RESOURCE.contains(s.getNome()) && !s.getNome().equals(name))
        throw ApiException.invalid("nome", "Status do sistema não pode ser renomeado");
      recursos
          .findByNomeIgnoreCase(name)
          .filter(x -> !Objects.equals(x.getCodigo(), id))
          .ifPresent(
              x -> {
                throw ApiException.invalid("nome", "Status já cadastrado");
              });
      s.setNome(name);
      return mapper.status(recursos.saveAndFlush(s));
    }
    var s = id == null ? new StatusReserva() : reserva(id);
    if (id != null && BOOKING.contains(s.getNome()) && !s.getNome().equals(name))
      throw ApiException.invalid("nome", "Status do sistema não pode ser renomeado");
    reservas
        .findByNomeIgnoreCase(name)
        .filter(x -> !Objects.equals(x.getCodigo(), id))
        .ifPresent(
            x -> {
              throw ApiException.invalid("nome", "Status já cadastrado");
            });
    s.setNome(name);
    return mapper.status(reservas.saveAndFlush(s));
  }

  public void delete(boolean resource, Long id) {
    if (resource) {
      var s = recurso(id);
      if (RESOURCE.contains(s.getNome()))
        throw ApiException.invalid("codigo", "Status do sistema não pode ser excluído");
      recursos.delete(s);
      recursos.flush();
    } else {
      var s = reserva(id);
      if (BOOKING.contains(s.getNome()))
        throw ApiException.invalid("codigo", "Status do sistema não pode ser excluído");
      reservas.delete(s);
      reservas.flush();
    }
  }

  @Override
  public void run(ApplicationArguments args) {
    for (String name : List.of("LIVRE", "OCUPADO", "BLOQUEADO"))
      if (recursos.findByNomeIgnoreCase(name).isEmpty()) {
        var s = new StatusRecurso();
        s.setNome(name);
        recursos.save(s);
      }
    for (String name : List.of("ATIVA", "CANCELADA", "CONCLUIDA"))
      if (reservas.findByNomeIgnoreCase(name).isEmpty()) {
        var s = new StatusReserva();
        s.setNome(name);
        reservas.save(s);
      }
  }
}
