package com.courtmaster.api.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RegistroRequest {
    private String nombre;
    private String email;
    private String password;
    private String telefono;
}