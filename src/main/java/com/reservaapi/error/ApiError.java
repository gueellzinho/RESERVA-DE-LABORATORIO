package com.reservaapi.error;

import java.time.Instant;
import java.util.List;

public record ApiError(
    Instant timestamp, int status, String mensagem, String caminho, List<FieldError> erros) {
  public record FieldError(String campo, String mensagem) {}

  public static ApiError of(int status, String message, String path, List<FieldError> errors) {
    return new ApiError(Instant.now(), status, message, path, errors);
  }
}
