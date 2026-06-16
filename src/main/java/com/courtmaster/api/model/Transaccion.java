package com.courtmaster.api.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacciones")
@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "pista_id", nullable = true)
    private Pista pista;

    @Enumerated(EnumType.STRING)
    private TipoTransaccion tipoTransaccion;

    private BigDecimal monto;
    private LocalDateTime fecha;
}