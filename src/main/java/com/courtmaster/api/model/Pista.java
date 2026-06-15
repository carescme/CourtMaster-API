package com.courtmaster.api.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "pistas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pista {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    @NotBlank(message = "El nombre de la pista es obligatorio.")
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "Debe especificar el tipo de pista (INDOOR, OUTDOOR).")
    private TipoPista tipo;

    @Column(nullable = false)
    private Boolean activa;

    @ManyToOne
    @JoinColumn(name = "club_id", nullable = false)
    @JsonBackReference
    private Club club;
}
