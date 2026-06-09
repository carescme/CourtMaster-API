package com.courtmaster.api.service;

import com.courtmaster.api.model.EstadoReserva;
import com.courtmaster.api.model.Pista;
import com.courtmaster.api.model.Reserva;
import com.courtmaster.api.model.Usuario;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.courtmaster.api.repository.ReservaRepository;
import com.courtmaster.api.repository.UsuarioRepository;
import com.courtmaster.api.repository.PistaRepository;

import lombok.RequiredArgsConstructor;
import java.util.List;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class ReservaService {
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PistaRepository pistaRepository;

    public List<Reserva> obtenerTodas(){
        return reservaRepository.findAll();
    }

    public List<Reserva> obtenerPorUsuario(Long usuarioId){
        return reservaRepository.findByUsuarioId(usuarioId);
    }

    @Transactional
    public Reserva crearReserva(Reserva reserva){
        Usuario usuarioDB = usuarioRepository.findById(reserva.getUsuario().getId())
            .orElseThrow(() -> new RuntimeException("No se puede reservar: El usuario no existe."));

        if (!usuarioDB.getActivo()) {
            throw new IllegalStateException("El usuario está desactivado.");
        }
        
        Pista pistaDB = pistaRepository.findById(reserva.getPista().getId())
            .orElseThrow(() -> new RuntimeException("No se puede reservar: La pista no existe."));
        
        if (!pistaDB.getActiva()) {
            throw new IllegalStateException("No se puede reservar: La pista seleccionada está desactivada.");
        }

        List<Reserva> reservasDia = reservaRepository.findByPistaIdAndFechaAndEstado(pistaDB.getId(), reserva.getFecha(), EstadoReserva.CONFIRMADA);

        boolean solapado = reservasDia.stream().anyMatch(existe ->
            reserva.getHoraInicio().isBefore(existe.getHoraFin()) &&
            reserva.getHoraFin().isAfter(existe.getHoraInicio())
        );

        if (solapado){
            throw new IllegalStateException("La pista ya está ocupada en ese horario.");
        }
        
        if (usuarioDB.getSaldo().compareTo(reserva.getPrecioPagado()) < 0){
            throw new IllegalStateException("Saldo insuficiente. La pista cuesta "+reserva.getPrecioPagado()+"€ y tu saldo es de "+usuarioDB.getSaldo()+"€.");
        }

        usuarioDB.setSaldo(usuarioDB.getSaldo().subtract(reserva.getPrecioPagado()));
        usuarioRepository.save(usuarioDB);

        reserva.setEstado(EstadoReserva.CONFIRMADA);
        return reservaRepository.save(reserva);
    }

    @Transactional
    public void cancelarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("No se puede cancelar: La reserva no existe."));

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new IllegalStateException("La reserva ya se encuentra cancelada.");
        }

        LocalDateTime momentoPartido = LocalDateTime.of(reserva.getFecha(), reserva.getHoraInicio());
        LocalDateTime fechaLimiteReembolsoTotal = momentoPartido.minusHours(24);
        LocalDateTime ahora = LocalDateTime.now();

        BigDecimal importeReembolso = BigDecimal.ZERO;

        if (ahora.isBefore(fechaLimiteReembolsoTotal)) {
            importeReembolso = reserva.getPrecioPagado();
            System.out.println("Cancelación a tiempo. Se reembolsa el 100%: " + importeReembolso + "€");
        } else {
            importeReembolso = reserva.getPrecioPagado().divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
            System.out.println("Cancelación tardía (menos de 24h). Se reembolsa el 50%: " + importeReembolso + "€");
        }

        if (importeReembolso.compareTo(BigDecimal.ZERO) > 0) {
            Usuario usuario = reserva.getUsuario();
            usuario.setSaldo(usuario.getSaldo().add(importeReembolso));
            usuarioRepository.save(usuario);
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);
    }
}
