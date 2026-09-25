package com.reservaapi.service;

import com.reservaapi.dto.Dtos.*;
import com.reservaapi.entities.Reserva;
import com.reservaapi.error.ApiException;
import com.reservaapi.mapper.ApiMapper;
import com.reservaapi.repositories.ReservaRepository;
import java.time.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservaService {
  private final ReservaRepository repo;
  private final RecursoService recursos;
  private final UsuarioService usuarios;
  private final StatusService statuses;
  private final ApiMapper mapper;
  private final Clock clock;

  private Reserva find(Long id) {
    return repo.findById(id).orElseThrow(() -> ApiException.missing("Reserva"));
  }

  private ReservaView view(Reserva r) {
    var d = mapper.reserva(r);
    var now = LocalDateTime.now(clock);
    var status = d.statusReserva();
    if (status.nome().equals("ATIVA")
        && !r.getDataFinal().atTime(r.getHoraFinal()).plusMinutes(1).isAfter(now))
      status = mapper.status(statuses.reserva("CONCLUIDA"));
    return new ReservaView(
        d.codigo(),
        d.dataInicial(),
        d.dataFinal(),
        d.horaInicial(),
        d.horaFinal(),
        d.usuario(),
        recursos.view(r.getRecurso(), now.toLocalDate(), now.toLocalTime()),
        status);
  }

  public ReservaView get(Long id) {
    return view(find(id));
  }

  public List<ReservaView> list(Map<String, String> f) {
    Filters.allowed(
        f,
        "recursoCodigo",
        "recursoNome",
        "data",
        "dataInicial",
        "dataFinal",
        "hora",
        "usuarioCodigo",
        "usuarioNome",
        "status",
        "statusReservaCodigo");
    var day = Filters.date(f.get("data"), "data");
    var from = Filters.date(f.get("dataInicial"), "dataInicial");
    var to = Filters.date(f.get("dataFinal"), "dataFinal");
    var time = Filters.time(f.get("hora"), "hora");
    if (from != null && to != null && to.isBefore(from))
      throw ApiException.invalid(
          "dataFinal", "Data Final precisa ser maior ou igual à Data Inicial");
    var spec =
        Filters.<Reserva>eq("recurso.codigo", Filters.id(f, "recursoCodigo"))
            .and(Filters.contains("recurso.nome", f.get("recursoNome")))
            .and(Filters.eq("dataInicial", day))
            .and(Filters.eq("usuario.codigo", Filters.id(f, "usuarioCodigo")))
            .and(Filters.contains("usuario.nomeCompleto", f.get("usuarioNome")));
    if (from != null)
      spec = spec.and((r, q, c) -> c.greaterThanOrEqualTo(r.get("dataInicial"), from));
    if (to != null) spec = spec.and((r, q, c) -> c.lessThanOrEqualTo(r.get("dataInicial"), to));
    if (time != null)
      spec =
          spec.and(
              (r, q, c) ->
                  c.and(
                      c.lessThanOrEqualTo(r.get("horaInicial"), time),
                      c.greaterThan(r.get("horaFinal"), time)));
    Long statusId = Filters.id(f, "statusReservaCodigo");
    String name = f.get("status");
    return repo.findAll(spec).stream()
        .map(this::view)
        .filter(d -> statusId == null || statusId.equals(d.statusReserva().codigo()))
        .filter(d -> name == null || StatusService.normalize(name).equals(d.statusReserva().nome()))
        .toList();
  }

  private void dates(ReservaInput d) {
    if (d.dataFinal().isBefore(d.dataInicial()))
      throw ApiException.invalid(
          "dataFinal", "Data Final precisa ser maior ou igual à Data Inicial");
    if (!d.dataFinal().equals(d.dataInicial()))
      throw ApiException.invalid("dataFinal", "A reserva é diária/por dia");
    if (!d.horaFinal().isAfter(d.horaInicial()))
      throw ApiException.invalid("horaFinal", "Hora Final precisa ser maior que a Hora Inicial");
    if (!d.dataInicial().atTime(d.horaInicial()).isAfter(LocalDateTime.now(clock)))
      throw ApiException.invalid("dataInicial", "A reserva deve começar no futuro");
  }

  public ReservaView save(Long id, ReservaInput d) {
    var r = id == null ? new Reserva() : find(id);
    // Lock both resources in a stable order to serialize concurrent booking changes.
    var ids = new TreeSet<Long>();
    ids.add(d.recursoCodigo());
    if (id != null) ids.add(r.getRecurso().getCodigo());
    ids.forEach(recursos::locked);
    String requested = statuses.reserva(d.statusReservaCodigo()).getNome();
    if (id != null) {
      if (!r.getStatusReserva().getNome().equals("ATIVA"))
        throw ApiException.invalid(
            "statusReservaCodigo", "Somente reservas ativas podem ser alteradas");
      if (requested.equals("CANCELADA")) {
        if (!sameBooking(r, d))
          throw ApiException.invalid(
              "reserva",
              "Para cancelar, preserve os dados originais ou use PATCH"
                  + " /api/reservas/{codigo}/cancelar");
        return cancelExisting(r);
      }
      if (!r.getDataInicial().atTime(r.getHoraInicial()).isAfter(LocalDateTime.now(clock)))
        throw ApiException.invalid("reserva", "Reserva iniciada não pode ser alterada");
      if (!sameBooking(r, d)
          && LocalDateTime.now(clock)
              .isAfter(r.getDataInicial().atTime(r.getHoraInicial()).minusHours(24)))
        throw ApiException.invalid(
            "reserva",
            "Alterações exigem antecedência mínima de 24 horas para preservar a regra de"
                + " cancelamento");
    }
    if (!requested.equals("ATIVA"))
      throw ApiException.invalid(
          "statusReservaCodigo",
          "Use ATIVA. Cancelamento possui operação própria e conclusão é automática");
    dates(d);
    var resource = recursos.find(d.recursoCodigo());
    if (!resource.getStatusRecurso().getNome().equals("LIVRE"))
      throw ApiException.invalid("recursoCodigo", "Recurso bloqueado ou indisponível");
    if (!repo.conflitos(d.recursoCodigo(), d.dataInicial(), d.horaInicial(), d.horaFinal(), id)
        .isEmpty())
      throw new ApiException(409, "recursoCodigo", "Recurso já reservado neste horário");
    r.setDataInicial(d.dataInicial());
    r.setDataFinal(d.dataFinal());
    r.setHoraInicial(d.horaInicial());
    r.setHoraFinal(d.horaFinal());
    r.setUsuario(usuarios.find(d.usuarioCodigo()));
    r.setRecurso(resource);
    r.setStatusReserva(statuses.reserva("ATIVA"));
    return view(repo.saveAndFlush(r));
  }

  private boolean sameBooking(Reserva r, ReservaInput d) {
    return r.getDataInicial().equals(d.dataInicial())
        && r.getDataFinal().equals(d.dataFinal())
        && r.getHoraInicial().equals(d.horaInicial())
        && r.getHoraFinal().equals(d.horaFinal())
        && r.getUsuario().getCodigo().equals(d.usuarioCodigo())
        && r.getRecurso().getCodigo().equals(d.recursoCodigo());
  }

  private ReservaView cancelExisting(Reserva r) {
    if (!r.getStatusReserva().getNome().equals("ATIVA"))
      throw ApiException.invalid(
          "statusReservaCodigo", "Somente reservas ativas podem ser canceladas");
    if (LocalDateTime.now(clock)
        .isAfter(r.getDataInicial().atTime(r.getHoraInicial()).minusHours(24)))
      throw ApiException.invalid(
          "reserva", "Cancelamento permitido somente com antecedência mínima de 24 horas");
    r.setStatusReserva(statuses.reserva("CANCELADA"));
    return view(repo.saveAndFlush(r));
  }

  public ReservaView cancel(Long id) {
    var r = find(id);
    recursos.locked(r.getRecurso().getCodigo());
    return cancelExisting(r);
  }

  public void delete(Long id) {
    var r = find(id);
    recursos.locked(r.getRecurso().getCodigo());
    if (r.getStatusReserva().getNome().equals("ATIVA")
        && r.getDataFinal()
            .atTime(r.getHoraFinal())
            .plusMinutes(1)
            .isAfter(LocalDateTime.now(clock))) cancelExisting(r);
    repo.delete(r);
    repo.flush();
  }

  public void conclude() {
    var limit = LocalDateTime.now(clock).minusMinutes(1);
    var data = limit.toLocalDate();
    var hora = limit.toLocalTime();

    for (var r : repo.vencidas(data)) {
      var finalReserva = r.getDataFinal().atTime(r.getHoraFinal());

      if (!finalReserva.isAfter(limit)) {
        r.setStatusReserva(statuses.reserva("CONCLUIDA"));
      }
    }
  }
}
