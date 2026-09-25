package com.reservaapi.service;

import com.reservaapi.config.DateConfig;
import com.reservaapi.error.ApiException;
import jakarta.persistence.criteria.*;
import java.time.*;
import java.util.*;
import org.springframework.data.jpa.domain.Specification;

public final class Filters {
  private Filters() {}

  public static LocalDate date(String value, String field) {
    if (value == null) return null;
    try {
      if (!value.matches("\\d{2}/\\d{2}/\\d{4}")) throw new DateTimeException("Formato");
      return LocalDate.parse(value, DateConfig.DATE);
    } catch (DateTimeException e) {
      throw ApiException.invalid(field, "Data Inválida");
    }
  }

  public static LocalTime time(String value, String field) {
    if (value == null) return null;
    try {
      if (!value.matches("\\d{2}:\\d{2}:\\d{2}")) throw new DateTimeException("Formato");
      return LocalTime.parse(value, DateConfig.TIME);
    } catch (DateTimeException e) {
      throw ApiException.invalid(field, "Hora Inválida");
    }
  }

  private static <T> Path<T> path(Root<?> root, String field) {
    Path<?> p = root;
    for (String part : field.split("\\.")) p = p.get(part);
    return (Path<T>) p;
  }

  public static <T> Specification<T> eq(String field, Object value) {
    return (r, q, c) -> value == null ? c.conjunction() : c.equal(path(r, field), value);
  }

  public static <T> Specification<T> contains(String field, String value) {
    return (r, q, c) ->
        value == null
            ? c.conjunction()
            : c.like(
                c.lower(path(r, field)),
                "%"
                    + value
                        .toLowerCase(Locale.ROOT)
                        .replace("!", "!!")
                        .replace("%", "!%")
                        .replace("_", "!_")
                    + "%",
                '!');
  }

  public static <T> Specification<T> named(String field, String value) {
    return (r, q, c) ->
        value == null
            ? c.conjunction()
            : c.equal(c.upper(path(r, field)), value.toUpperCase(Locale.ROOT));
  }

  public static void allowed(Map<String, String> values, String... names) {
    var allowed = Set.of(names);
    for (String key : values.keySet())
      if (!allowed.contains(key)) throw ApiException.invalid(key, "Filtro desconhecido");
  }

  public static Long id(Map<String, String> values, String name) {
    try {
      return values.containsKey(name) ? Long.valueOf(values.get(name)) : null;
    } catch (NumberFormatException e) {
      throw ApiException.invalid(name, "Valor inválido");
    }
  }
}
