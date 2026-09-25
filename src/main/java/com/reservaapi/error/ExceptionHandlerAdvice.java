package com.reservaapi.error;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.*;
import java.util.*;
import org.slf4j.*;
import org.springframework.dao.*;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class ExceptionHandlerAdvice {
  private static final Logger log = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);

  private ResponseEntity<ApiError> response(
      int status, String msg, String path, List<ApiError.FieldError> fields) {
    return ResponseEntity.status(status).body(ApiError.of(status, msg, path, fields));
  }

  @ExceptionHandler(ApiException.class)
  ResponseEntity<ApiError> business(ApiException e, HttpServletRequest r) {
    return response(
        e.getStatus(),
        e.getMessage(),
        r.getRequestURI(),
        List.of(new ApiError.FieldError(e.getCampo(), e.getMessage())));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ApiError> validation(MethodArgumentNotValidException e, HttpServletRequest r) {
    var fields =
        e.getBindingResult().getFieldErrors().stream()
            .map(f -> new ApiError.FieldError(f.getField(), f.getDefaultMessage()))
            .distinct()
            .toList();
    return response(422, "Erro de validação", r.getRequestURI(), fields);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  ResponseEntity<ApiError> json(HttpMessageNotReadableException e, HttpServletRequest r) {
    String field = "corpo", msg = "JSON inválido: confira os campos e tipos informados";
    for (Throwable t = e; t != null; t = t.getCause()) {
      if (t instanceof JsonMappingException m && !m.getPath().isEmpty())
        field = Objects.toString(m.getPath().getLast().getFieldName(), "corpo");
      if (t instanceof InvalidFormatException f) {
        if (f.getTargetType() == LocalDate.class) msg = "Data Inválida";
        else if (f.getTargetType() == LocalTime.class) msg = "Hora Inválida";
      }
    }
    return response(
        422, "Erro de validação", r.getRequestURI(), List.of(new ApiError.FieldError(field, msg)));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  ResponseEntity<ApiError> parameter(MethodArgumentTypeMismatchException e, HttpServletRequest r) {
    return response(
        422,
        "Parâmetro inválido",
        r.getRequestURI(),
        List.of(new ApiError.FieldError(e.getName(), "Valor inválido")));
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  ResponseEntity<ApiError> integrity(DataIntegrityViolationException e, HttpServletRequest r) {
    return response(
        409, "Registro duplicado ou vinculado a outros registros", r.getRequestURI(), List.of());
  }

  @ExceptionHandler({
    OptimisticLockingFailureException.class,
    PessimisticLockingFailureException.class
  })
  ResponseEntity<ApiError> concurrent(Exception e, HttpServletRequest r) {
    return response(
        409,
        "Registro alterado simultaneamente. Atualize os dados e tente novamente",
        r.getRequestURI(),
        List.of());
  }

  @ExceptionHandler(AuthenticationException.class)
  ResponseEntity<ApiError> login(AuthenticationException e, HttpServletRequest r) {
    return response(401, "Login ou senha inválidos", r.getRequestURI(), List.of());
  }

  @ExceptionHandler(AccessDeniedException.class)
  ResponseEntity<ApiError> forbidden(AccessDeniedException e, HttpServletRequest r) {
    return response(403, "Acesso negado", r.getRequestURI(), List.of());
  }

  @ExceptionHandler({
    NoResourceFoundException.class,
    HttpRequestMethodNotSupportedException.class,
    HttpMediaTypeNotSupportedException.class
  })
  ResponseEntity<ApiError> http(Exception e, HttpServletRequest r) {
    int status = ((ErrorResponse) e).getStatusCode().value();
    String message =
        status == 404
            ? "Endpoint não encontrado"
            : status == 405
                ? "Método HTTP não permitido"
                : "Tipo de conteúdo não suportado. Use application/json";
    return response(status, message, r.getRequestURI(), List.of());
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<ApiError> unexpected(Exception e, HttpServletRequest r) {
    log.error("Falha inesperada em {}", r.getRequestURI(), e);
    return response(500, "Erro interno ao processar a solicitação", r.getRequestURI(), List.of());
  }
}
