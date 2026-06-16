package com.courtmaster.api.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class RecargaRequest {
    private BigDecimal monto;
}