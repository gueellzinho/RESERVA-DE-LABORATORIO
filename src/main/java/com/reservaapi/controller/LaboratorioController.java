package com.reservaapi.controller;

import com.reservaapi.dto.Dtos.*;
import com.reservaapi.service.RecursoService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/laboratorios")
@RequiredArgsConstructor
public class LaboratorioController {
  private final RecursoService service;

  @GetMapping
  public List<RecursoView> list(@RequestParam Map<String, String> f) {
    return service.list("LABORATORIO", f, false);
  }

  @GetMapping("/{id}")
  public RecursoView get(@PathVariable Long id) {
    return service.get(id, "LABORATORIO");
  }

  @PostMapping
  public ResponseEntity<RecursoView> create(@Valid @RequestBody RecursoInput d) {
    var r = service.save(null, "LABORATORIO", d);
    return ResponseEntity.created(URI.create("/api/laboratorios/" + r.codigo())).body(r);
  }

  @PutMapping("/{id}")
  public RecursoView update(@PathVariable Long id, @Valid @RequestBody RecursoInput d) {
    return service.save(id, "LABORATORIO", d);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id, "LABORATORIO");
    return ResponseEntity.noContent().build();
  }
}
