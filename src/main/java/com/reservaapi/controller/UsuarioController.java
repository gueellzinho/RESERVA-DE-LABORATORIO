package com.reservaapi.controller;

import com.reservaapi.dto.Dtos.*;
import com.reservaapi.service.UsuarioService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
  private final UsuarioService service;

  @GetMapping
  public List<UsuarioView> list(@RequestParam Map<String, String> f) {
    return service.list(f);
  }

  @GetMapping("/{id}")
  public UsuarioView get(@PathVariable Long id) {
    return service.get(id);
  }

  @PostMapping
  public ResponseEntity<UsuarioView> create(@Valid @RequestBody UsuarioInput d) {
    var u = service.save(null, d);
    return ResponseEntity.created(URI.create("/api/usuarios/" + u.codigo())).body(u);
  }

  @PutMapping("/{id}")
  public UsuarioView update(@PathVariable Long id, @Valid @RequestBody UsuarioInput d) {
    return service.save(id, d);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
