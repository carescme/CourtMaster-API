package com.courtmaster.api.dto;

import com.courtmaster.api.model.TipoPista;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubDTO {
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private List<PistaInfoDTO> pistas;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PistaInfoDTO {
        private Long id;
        private String nombre;
        private TipoPista tipo;
        private boolean activa;
    }
}