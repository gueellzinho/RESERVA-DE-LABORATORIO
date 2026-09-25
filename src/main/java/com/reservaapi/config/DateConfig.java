package com.reservaapi.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.ser.*;
import java.io.IOException;
import java.time.*;
import java.time.format.*;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.*;

@Configuration
public class DateConfig {
  public static final DateTimeFormatter DATE =
      DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);
  public static final DateTimeFormatter TIME =
      DateTimeFormatter.ofPattern("HH:mm:ss").withResolverStyle(ResolverStyle.STRICT);

  @Bean
  Jackson2ObjectMapperBuilderCustomizer dateFormats() {
    return builder -> {
      builder.serializers(new LocalDateSerializer(DATE), new LocalTimeSerializer(TIME));
      builder.deserializerByType(
          LocalDate.class,
          new JsonDeserializer<LocalDate>() {
            public LocalDate deserialize(JsonParser p, DeserializationContext ctx)
                throws IOException {
              String s = p.getValueAsString();
              try {
                if (s == null || !s.matches("\\d{2}/\\d{2}/\\d{4}"))
                  throw new DateTimeException("Formato");
                return LocalDate.parse(s, DATE);
              } catch (DateTimeException e) {
                return (LocalDate) ctx.handleWeirdStringValue(LocalDate.class, s, "Data Inválida");
              }
            }
          });
      builder.deserializerByType(
          LocalTime.class,
          new JsonDeserializer<LocalTime>() {
            public LocalTime deserialize(JsonParser p, DeserializationContext ctx)
                throws IOException {
              String s = p.getValueAsString();
              try {
                if (s == null || !s.matches("\\d{2}:\\d{2}:\\d{2}"))
                  throw new DateTimeException("Formato");
                return LocalTime.parse(s, TIME);
              } catch (DateTimeException e) {
                return (LocalTime) ctx.handleWeirdStringValue(LocalTime.class, s, "Hora Inválida");
              }
            }
          });
    };
  }
}
