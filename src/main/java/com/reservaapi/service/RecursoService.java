package com.reservaapi.service;

import com.reservaapi.dto.Dtos.*;
import com.reservaapi.entities.*;
import com.reservaapi.error.ApiException;
import com.reservaapi.mapper.ApiMapper;
import com.reservaapi.repositories.*;
import java.time.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RecursoService {
  private final RecursoRepository repo;
  private final ReservaRepository reservas;
  private final StatusService statuses;
  private final ApiMapper mapper;
  private final Clock clock;

  public Recurso find(Long id) {
    return repo.findById(id).orElseThrow(() -> ApiException.missing("Recurso"));
  }

  public Recurso locked(Long id) {
    return repo.findLocked(id).orElseThrow(() -> ApiException.missing("Recurso"));
  }

  private void checkType(Recurso r, String type) {
    if (type != null
        && !(type.equals("LABORATORIO") ? r instanceof Laboratorio : r instanceof Sala))
      throw ApiException.missing("Recurso");
  }

  public RecursoView view(Recurso r, LocalDate date, LocalTime time) {
    var dto = mapper.recurso(r);
    var status = dto.statusRecurso();
    if (status.nome().equals("LIVRE") && reservas.ocupado(r.getCodigo(), date, time))
      status = mapper.status(statuses.recurso("OCUPADO"));
    return new RecursoView(
        dto.codigo(), dto.tipo(), dto.nome(), dto.capacidade(), dto.localizacao(), status);
  }

  public RecursoView get(Long id, String type) {
    var r = find(id);
    checkType(r, type);
    var now = LocalDateTime.now(clock);
    return view(r, now.toLocalDate(), now.toLocalTime());
  }

  public List<RecursoView> list(String type, Map<String, String> f, boolean available) {
    Filters.allowed(
        f,
        "nome",
        "capacidade",
        "localizacao",
        "status",
        "statusRecursoCodigo",
        "data",
        "hora",
        "horaInicial",
        "horaFinal");
    var now = LocalDateTime.now(clock);
    var date = Filters.date(f.get("data"), "data");
    var time = Filters.time(f.get("hora"), "hora");
    var start = Filters.time(f.get("horaInicial"), "horaInicial");
    var end = Filters.time(f.get("horaFinal"), "horaFinal");
    if (available && (date == null || start == null || end == null))
      throw ApiException.invalid("periodo", "Informe data, horaInicial e horaFinal");
    if ((start == null) != (end == null))
      throw ApiException.invalid("periodo", "Informe horaInicial e horaFinal juntas");
    if (start != null && !end.isAfter(start))
      throw ApiException.invalid("horaFinal", "Hora Final precisa ser maior que a Hora Inicial");
    if (!available && start != null)
      throw ApiException.invalid(
          "periodo", "Use /api/recursos/disponiveis para pesquisar um intervalo");
    if (available && time != null)
      throw ApiException.invalid(
          "hora", "Use horaInicial e horaFinal na consulta de disponibilidade");
    if (date == null) date = now.toLocalDate();
    if (time == null) time = start == null ? now.toLocalTime() : start;
    final LocalDate day = date;
    final LocalTime hour = time;
    Long statusId = Filters.id(f, "statusRecursoCodigo");
    String statusName = f.get("status");
    var spec =
        Filters.<Recurso>contains("nome", f.get("nome"))
            .and(Filters.eq("capacidade", Filters.id(f, "capacidade")))
            .and(Filters.contains("localizacao", f.get("localizacao")));
    var result = new ArrayList<RecursoView>();
    for (var r : repo.findAll(spec)) {
      if (type != null
          && !(type.equals("LABORATORIO") ? r instanceof Laboratorio : r instanceof Sala)) continue;
      if (available
          && (!r.getStatusRecurso().getNome().equals("LIVRE")
              || !reservas.conflitos(r.getCodigo(), day, start, end, null).isEmpty())) continue;
      var dto = view(r, day, hour);
      if (statusId != null && !statusId.equals(dto.statusRecurso().codigo())) continue;
      if (statusName != null
          && !StatusService.normalize(statusName).equals(dto.statusRecurso().nome())) continue;
      result.add(dto);
    }
    return result;
  }

  public RecursoView save(Long id, String type, RecursoInput d) {
    Recurso r =
        id == null ? (type.equals("LABORATORIO") ? new Laboratorio() : new Sala()) : locked(id);
    checkType(r, type);
    var status = statuses.recurso(d.statusRecursoCodigo());
    if (status.getNome().equals("OCUPADO"))
      throw ApiException.invalid(
          "statusRecursoCodigo", "OCUPADO é calculado automaticamente pelas reservas");
    if (!status.getNome().equals("LIVRE")
        && id != null
        && reservas.existsByRecurso_CodigoAndStatusReserva_NomeIgnoreCase(id, "ATIVA"))
      throw ApiException.invalid(
          "statusRecursoCodigo",
          "Recurso com reservas ativas não pode ser bloqueado ou indisponibilizado");
    r.setNome(d.nome());
    r.setCapacidade(d.capacidade());
    r.setLocalizacao(d.localizacao());
    r.setStatusRecurso(status);
    repo.saveAndFlush(r);
    var now = LocalDateTime.now(clock);
    return view(r, now.toLocalDate(), now.toLocalTime());
  }

  public void delete(Long id, String type) {
    var r = locked(id);
    checkType(r, type);
    repo.delete(r);
    repo.flush();
  }
}
