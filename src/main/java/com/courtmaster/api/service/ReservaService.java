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

        List<Reserva> reservasDia = reservaRepository.findByPistaIdAndFecha(pistaDB.getId(), reserva.getFecha());

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
}
