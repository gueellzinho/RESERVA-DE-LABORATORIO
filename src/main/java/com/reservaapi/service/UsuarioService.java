package com.reservaapi.service;

import com.reservaapi.dto.Dtos.*;
import com.reservaapi.entities.Usuario;
import com.reservaapi.error.ApiException;
import com.reservaapi.mapper.ApiMapper;
import com.reservaapi.repositories.UsuarioRepository;
import java.nio.charset.StandardCharsets;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {
  private final UsuarioRepository repo;
  private final ApiMapper mapper;
  private final PasswordEncoder encoder;

  public Usuario find(Long id) {
    return repo.findById(id).orElseThrow(() -> ApiException.missing("Usuário"));
  }

  public UsuarioView get(Long id) {
    return mapper.usuario(find(id));
  }

  public List<UsuarioView> list(Map<String, String> f) {
    Filters.allowed(f, "cpf", "nome", "email", "dataAniversario");
    return repo
        .findAll(
            Filters.<Usuario>eq("cpf", f.get("cpf"))
                .and(Filters.contains("nomeCompleto", f.get("nome")))
                .and(Filters.contains("email", f.get("email")))
                .and(
                    Filters.eq(
                        "dataAniversario",
                        Filters.date(f.get("dataAniversario"), "dataAniversario"))))
        .stream()
        .map(mapper::usuario)
        .toList();
  }

  public UsuarioView save(Long id, UsuarioInput d) {
    var user = id == null ? new Usuario() : find(id);
    repo.findByCpf(d.cpf())
        .filter(u -> !Objects.equals(u.getCodigo(), id))
        .ifPresent(
            u -> {
              throw ApiException.invalid("cpf", "CPF já cadastrado");
            });
    String email = d.email().trim().toLowerCase(Locale.ROOT),
        login = d.login().toLowerCase(Locale.ROOT);
    repo.findByEmailIgnoreCase(email)
        .filter(u -> !Objects.equals(u.getCodigo(), id))
        .ifPresent(
            u -> {
              throw ApiException.invalid("email", "E-mail já cadastrado");
            });
    repo.findByLoginIgnoreCase(login)
        .filter(u -> !Objects.equals(u.getCodigo(), id))
        .ifPresent(
            u -> {
              throw ApiException.invalid("login", "Login já cadastrado");
            });
    if (d.senha().getBytes(StandardCharsets.UTF_8).length > 72)
      throw ApiException.invalid("senha", "Senha deve ter no máximo 72 bytes UTF-8");
    user.setCpf(d.cpf());
    user.setNomeCompleto(d.nomeCompleto());
    user.setDataAniversario(d.dataAniversario());
    user.setCelular(d.celular());
    user.setEmail(email);
    user.setLogin(login);
    user.setSenha(encoder.encode(d.senha()));
    return mapper.usuario(repo.saveAndFlush(user));
  }

  public void delete(Long id) {
    repo.delete(find(id));
    repo.flush();
  }
}
