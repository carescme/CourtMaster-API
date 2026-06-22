package com.courtmaster.api.controller;

import com.courtmaster.api.model.Usuario;
import com.courtmaster.api.service.DashboardOwner;
import com.courtmaster.api.dto.DashboardReserva;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clubes/mi-club")
@RequiredArgsConstructor
public class DashboardOwnerController {

    private final DashboardOwner dashboardService;

    @GetMapping("/reservas")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<DashboardReserva>> getReservasMiClub(
            @AuthenticationPrincipal Usuario usuarioLogueado) {
        
        List<DashboardReserva> reservas = dashboardService.obtenerReservasMiClub(usuarioLogueado);
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/ingresos")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Map<String, Object>> getIngresosMiClub(@AuthenticationPrincipal Usuario usuarioLogueado) {
        Map<String, Object> ingresos = dashboardService.calcularIngresosMiClub(usuarioLogueado);
        return ResponseEntity.ok(ingresos);
    }
}