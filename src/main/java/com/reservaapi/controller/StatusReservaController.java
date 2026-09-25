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
@RequestMapping("/api/status-reservas")
@RequiredArgsConstructor
public class StatusReservaController {
  private final StatusService service;

  @GetMapping
  public List<StatusView> list() {
    return service.list(false);
  }

  @GetMapping("/{id}")
  public StatusView get(@PathVariable Long id) {
    return service.get(false, id);
  }

  @PostMapping
  public ResponseEntity<StatusView> create(@Valid @RequestBody StatusInput d) {
    var s = service.save(false, null, d);
    return ResponseEntity.created(URI.create("/api/status-reservas/" + s.codigo())).body(s);
  }

  @PutMapping("/{id}")
  public StatusView update(@PathVariable Long id, @Valid @RequestBody StatusInput d) {
    return service.save(false, id, d);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(false, id);
    return ResponseEntity.noContent().build();
  }
}
