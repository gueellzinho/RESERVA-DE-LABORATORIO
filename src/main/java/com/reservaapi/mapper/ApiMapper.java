package com.reservaapi.mapper;

import com.reservaapi.dto.Dtos.*;
import com.reservaapi.entities.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ApiMapper {
  UsuarioView usuario(Usuario entity);

  StatusView status(StatusRecurso entity);

  StatusView status(StatusReserva entity);

  @Mapping(
      target = "tipo",
      expression =
          "java(org.hibernate.Hibernate.getClass(entity) =="
              + " com.reservaapi.entities.Laboratorio.class ? \"LABORATORIO\" : \"SALA\")")
  RecursoView recurso(Recurso entity);

  ReservaView reserva(Reserva entity);
}
