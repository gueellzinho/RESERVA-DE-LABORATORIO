package com.reservaapi.controller;

import com.reservaapi.dto.Dtos.*;
import com.reservaapi.mapper.ApiMapper;
import com.reservaapi.repositories.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthenticationManager authentication;
  private final UsuarioRepository users;
  private final ApiMapper mapper;

  @PostMapping("/login")
  public LoginView login(@Valid @RequestBody LoginInput d) {
    var result =
        authentication.authenticate(
            UsernamePasswordAuthenticationToken.unauthenticated(d.login(), d.senha()));
    var user =
        users
            .findByLoginIgnoreCase(result.getName())
            .orElseThrow(() -> new BadCredentialsException("Login ou senha inválidos"));
    return new LoginView("Login realizado com sucesso", "Basic", mapper.usuario(user));
  }
}
