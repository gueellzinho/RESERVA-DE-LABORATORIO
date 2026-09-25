package com.reservaapi.repositories;

import com.reservaapi.entities.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UsuarioRepository
    extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {

  Optional<Usuario> findByCpf(String cpf);

  Optional<Usuario> findByEmailIgnoreCase(String email);

  Optional<Usuario> findByLoginIgnoreCase(String login);

  boolean existsByCpf(String cpf);

  boolean existsByEmailIgnoreCase(String email);
}
