package com.reservaapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConclusaoScheduler {
  private final ReservaService service;

  @Scheduled(fixedDelayString = "${app.conclusao-intervalo-ms:1000}", initialDelay = 3000)
  public void conclude() {
    service.conclude();
  }
}
