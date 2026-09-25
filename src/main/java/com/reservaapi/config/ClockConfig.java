package com.reservaapi.config;

import java.time.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
public class ClockConfig {
  @Bean
  public Clock clock(@Value("${app.timezone}") String zone) {
    return Clock.system(ZoneId.of(zone));
  }
}
