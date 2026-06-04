package com.courtmaster.api.model;

import jakarta.persistence.*;
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
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoPista tipo;

    @Column(nullable = false)
    private boolean activa;
}
