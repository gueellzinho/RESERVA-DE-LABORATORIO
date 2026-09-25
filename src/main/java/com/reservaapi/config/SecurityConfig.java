package com.reservaapi.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reservaapi.error.ApiError;
import com.reservaapi.repositories.UsuarioRepository;
import java.util.List;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  UserDetailsService userDetailsService(UsuarioRepository users) {
    return login ->
        users
            .findByLoginIgnoreCase(login)
            .map(
                u ->
                    User.withUsername(u.getLogin()).password(u.getSenha()).roles("USUARIO").build())
            .orElseThrow(() -> new UsernameNotFoundException("Login ou senha inválidos"));
  }

  @Bean
  AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

  @Bean
  SecurityFilterChain security(HttpSecurity http, ObjectMapper json) throws Exception {
    return http.csrf(c -> c.disable())
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            a ->
                a.requestMatchers(HttpMethod.POST, "/api/usuarios", "/api/auth/login")
                    .permitAll()
                    .requestMatchers("/error")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .httpBasic(
            b ->
                b.authenticationEntryPoint(
                    (request, response, e) -> {
                      response.setStatus(401);
                      response.setContentType("application/json;charset=UTF-8");
                      response.setHeader("WWW-Authenticate", "Basic realm=\"Reserva API\"");
                      json.writeValue(
                          response.getOutputStream(),
                          ApiError.of(
                              401, "Login ou senha inválidos", request.getRequestURI(), List.of()));
                    }))
        .exceptionHandling(
            h ->
                h.accessDeniedHandler(
                    (request, response, e) -> {
                      response.setStatus(403);
                      response.setContentType("application/json;charset=UTF-8");
                      json.writeValue(
                          response.getOutputStream(),
                          ApiError.of(403, "Acesso negado", request.getRequestURI(), List.of()));
                    }))
        .build();
  }
}
