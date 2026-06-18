package com.courtmaster.api.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class ClubRequest {
    @NotBlank(message = "El nombre del club no puede estar vacío.")
    private String nombre;

    @NotBlank(message = "La ubicación no puede estar vacía.")
    private String ubicacion;

    @NotNull(message = "Debes asignar un propietario (OWNER) al club.")
    private Long ownerId;
}