package com.reservaapi.error;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
  private final int status;
  private final String campo;

  public ApiException(int status, String campo, String mensagem) {
    super(mensagem);
    this.status = status;
    this.campo = campo;
  }

  public static ApiException invalid(String campo, String mensagem) {
    return new ApiException(422, campo, mensagem);
  }

  public static ApiException missing(String entidade) {
    return new ApiException(404, "codigo", entidade + " não encontrado(a)");
  }
}
