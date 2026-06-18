package com.courtmaster.api.controller;

import com.courtmaster.api.dto.DashboardReserva;
import com.courtmaster.api.model.Reserva;
import com.courtmaster.api.service.ReservaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {
    private final ReservaService reservaService;

    //GET
    @GetMapping
    public List<Reserva> listarTodas(){
        return reservaService.obtenerTodas();
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Reserva> listarPorUsuario(@PathVariable Long usuarioId) {
        return reservaService.obtenerPorUsuario(usuarioId);
    }
    
    //POST
    @PostMapping
    public ResponseEntity<DashboardReserva> crear(
            @Valid @RequestBody Reserva reserva, 
            @AuthenticationPrincipal UserDetails userDetails) {
        
        String email = userDetails.getUsername();
        Reserva nuevaReserva = reservaService.crearReserva(reserva, email);
        
        DashboardReserva dto = DashboardReserva.builder()
                .id(nuevaReserva.getId())
                .usuarioEmail(nuevaReserva.getUsuario().getEmail())
                .usuarioTelefono(nuevaReserva.getUsuario().getTelefono())
                .pistaId(nuevaReserva.getPista().getId())
                .pistaNombre(nuevaReserva.getPista().getNombre())
                .fecha(nuevaReserva.getFecha())
                .horaInicio(nuevaReserva.getHoraInicio())
                .horaFin(nuevaReserva.getHoraFin())
                .precioPagado(nuevaReserva.getPrecioPagado())
                .estado(nuevaReserva.getEstado().name())
                .build();

        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    //DELETE
    @DeleteMapping("/{id}")
    public String cancelar(@PathVariable Long id){
        reservaService.cancelarReserva(id);
        return "Reserva con ID "+id+" cancelada correctamente. Saldo devuelto al usuario.";
    }
}