package com.courtmaster.api.dto;

import com.courtmaster.api.model.TipoTransaccion;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransaccionResponse {
    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private Long pistaId;
    private String pistaNombre;
    private TipoTransaccion tipoTransaccion;
    private BigDecimal monto;
    private LocalDateTime fecha;
}