package com.courtmaster.api.controller;

import com.courtmaster.api.model.Reserva;
import com.courtmaster.api.service.ReservaService;

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
    public Reserva crear(@RequestBody Reserva reserva, @AuthenticationPrincipal UserDetails userDetails){
        String email = userDetails.getUsername();
        return reservaService.crearReserva(reserva, email);
    }

    //DELETE
    @DeleteMapping("/{id}")
    public String cancelar(@PathVariable Long id){
        reservaService.cancelarReserva(id);
        return "Reserva con ID "+id+" cancelada correctamente. Saldo devuelto al usuario.";
    }
}
