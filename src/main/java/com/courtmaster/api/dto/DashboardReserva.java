package com.courtmaster.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardReserva {
    private Long id;
    
    private String usuarioEmail;
    private String usuarioTelefono;

    private Long pistaId;
    private String pistaNombre;

    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private BigDecimal precioPagado;
    private String estado;
}