package com.reservaapi.controller;

import com.reservaapi.dto.Dtos.*;
import com.reservaapi.service.StatusService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/status-recursos")
@RequiredArgsConstructor
public class StatusRecursoController {
  private final StatusService service;

  @GetMapping
  public List<StatusView> list() {
    return service.list(true);
  }

  @GetMapping("/{id}")
  public StatusView get(@PathVariable Long id) {
    return service.get(true, id);
  }

  @PostMapping
  public ResponseEntity<StatusView> create(@Valid @RequestBody StatusInput d) {
    var s = service.save(true, null, d);
    return ResponseEntity.created(URI.create("/api/status-recursos/" + s.codigo())).body(s);
  }

  @PutMapping("/{id}")
  public StatusView update(@PathVariable Long id, @Valid @RequestBody StatusInput d) {
    return service.save(true, id, d);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(true, id);
    return ResponseEntity.noContent().build();
  }
}
