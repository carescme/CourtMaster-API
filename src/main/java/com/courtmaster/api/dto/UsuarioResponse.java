package com.courtmaster.api.dto;

import com.courtmaster.api.model.Rol;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class UsuarioResponse {
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private Rol rol;
    private BigDecimal saldo;
    private boolean activo;
    private Long clubId; 
}