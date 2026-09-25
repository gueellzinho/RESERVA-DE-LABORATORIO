package com.reservaapi.controller;

import com.reservaapi.dto.Dtos.*;
import com.reservaapi.service.RecursoService;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recursos")
@RequiredArgsConstructor
public class RecursoController {
  private final RecursoService service;

  @GetMapping
  public List<RecursoView> list(@RequestParam Map<String, String> f) {
    return service.list(null, f, false);
  }

  @GetMapping("/disponiveis")
  public List<RecursoView> available(@RequestParam Map<String, String> f) {
    return service.list(null, f, true);
  }

  @GetMapping("/{id}")
  public RecursoView get(@PathVariable Long id) {
    return service.get(id, null);
  }
}
