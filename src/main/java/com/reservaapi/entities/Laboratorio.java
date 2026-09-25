package com.reservaapi.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "laboratorio")
@DiscriminatorValue("LABORATORIO")
@PrimaryKeyJoinColumn(name = "codigo")
@NoArgsConstructor
@SuperBuilder
public class Laboratorio extends Recurso {}
