package com.reservaapi.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "sala")
@DiscriminatorValue("SALA")
@PrimaryKeyJoinColumn(name = "codigo")
@NoArgsConstructor
@SuperBuilder
public class Sala extends Recurso {
}
