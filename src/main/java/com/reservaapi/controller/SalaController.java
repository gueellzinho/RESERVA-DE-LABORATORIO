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
@RequestMapping("/api/salas")
@RequiredArgsConstructor
public class SalaController {
  private final RecursoService service;

  @GetMapping
  public List<RecursoView> list(@RequestParam Map<String, String> f) {
    return service.list("SALA", f, false);
  }

  @GetMapping("/{id}")
  public RecursoView get(@PathVariable Long id) {
    return service.get(id, "SALA");
  }

  @PostMapping
  public ResponseEntity<RecursoView> create(@Valid @RequestBody RecursoInput d) {
    var r = service.save(null, "SALA", d);
    return ResponseEntity.created(URI.create("/api/salas/" + r.codigo())).body(r);
  }

  @PutMapping("/{id}")
  public RecursoView update(@PathVariable Long id, @Valid @RequestBody RecursoInput d) {
    return service.save(id, "SALA", d);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id, "SALA");
    return ResponseEntity.noContent().build();
  }
}
