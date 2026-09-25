package com.reservaapi.controller;

import com.reservaapi.dto.Dtos.*;
import com.reservaapi.service.ReservaService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {
  private final ReservaService service;

  @GetMapping
  public List<ReservaView> list(@RequestParam Map<String, String> f) {
    return service.list(f);
  }

  @GetMapping("/{id}")
  public ReservaView get(@PathVariable Long id) {
    return service.get(id);
  }

  @PostMapping
  public ResponseEntity<ReservaView> create(@Valid @RequestBody ReservaInput d) {
    var r = service.save(null, d);
    return ResponseEntity.created(URI.create("/api/reservas/" + r.codigo())).body(r);
  }

  @PutMapping("/{id}")
  public ReservaView update(@PathVariable Long id, @Valid @RequestBody ReservaInput d) {
    return service.save(id, d);
  }

  @PatchMapping("/{id}/cancelar")
  public ReservaView cancel(@PathVariable Long id) {
    return service.cancel(id);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
