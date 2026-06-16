package com.courtmaster.api.controller;

import com.courtmaster.api.dto.TransaccionResponse;
import com.courtmaster.api.model.Usuario;
import com.courtmaster.api.service.TransaccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/transacciones")
@RequiredArgsConstructor
public class TransaccionController {

    private final TransaccionService transaccionService;

    @GetMapping("/mi-historial")
    public ResponseEntity<List<TransaccionResponse>> obtenerMiHistorial(@AuthenticationPrincipal Usuario usuarioLogueado) {
        List<TransaccionResponse> historialDTO = transaccionService.obtenerHistorialUsuario(usuarioLogueado.getId())
                .stream()
                .map(t -> TransaccionResponse.builder()
                        .id(t.getId())
                        .usuarioId(t.getUsuario().getId())
                        .usuarioNombre(t.getUsuario().getNombre())
                        .pistaId(t.getPista() != null ? t.getPista().getId() : null)
                        .pistaNombre(t.getPista() != null ? t.getPista().getNombre() : "N/A")
                        .tipoTransaccion(t.getTipoTransaccion())
                        .monto(t.getMonto())
                        .fecha(t.getFecha())
                        .build()
                ).collect(Collectors.toList());

        return ResponseEntity.ok(historialDTO);
    }
}