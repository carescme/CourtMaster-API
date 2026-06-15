package com.courtmaster.api.service;

import com.courtmaster.api.model.EstadoReserva;
import com.courtmaster.api.model.Pista;
import com.courtmaster.api.model.Reserva;
import com.courtmaster.api.model.Usuario;
import com.courtmaster.api.repository.ReservaRepository;
import com.courtmaster.api.repository.UsuarioRepository;
import com.courtmaster.api.repository.PistaRepository;
import com.courtmaster.api.exception.BadRequestException;
import com.courtmaster.api.exception.ConflictException;
import com.courtmaster.api.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    @Transactional(readOnly = true)
    public List<Reserva> obtenerTodas(){
        return reservaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Reserva> obtenerPorUsuario(Long usuarioId){
        // Validación preventiva: verificar si el usuario existe antes de buscar sus reservas
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("El usuario con ID " + usuarioId + " no existe.");
        }
        return reservaRepository.findByUsuarioId(usuarioId);
    }

    @Transactional
    public Reserva crearReserva(Reserva reserva, String email){
        Usuario usuarioDB = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("No se puede reservar: El usuario con email " + email + " no existe."));

        if (!usuarioDB.getActivo()) {
            throw new BadRequestException("No se puede reservar: El usuario está desactivado.");
        }

        if (reserva.getPista() == null || reserva.getPista().getId() == null) {
            throw new BadRequestException("No se puede reservar: Debe especificar una pista válida.");
        }

        Pista pistaDB = pistaRepository.findById(reserva.getPista().getId())
            .orElseThrow(() -> new ResourceNotFoundException("No se puede reservar: La pista especificada no existe."));
        
        if (!pistaDB.getActiva()) {
            throw new BadRequestException("No se puede reservar: La pista seleccionada está desactivada.");
        }

        if (reserva.getFecha() == null || reserva.getHoraInicio() == null || reserva.getHoraFin() == null) {
            throw new BadRequestException("Debe especificar la fecha, hora de inicio y hora de fin de la reserva.");
        }
        if (reserva.getHoraInicio().isAfter(reserva.getHoraFin()) || reserva.getHoraInicio().equals(reserva.getHoraFin())) {
            throw new BadRequestException("La hora de inicio debe ser anterior a la hora de fin.");
        }

        reserva.setUsuario(usuarioDB);
        reserva.setPista(pistaDB);

        LocalDateTime momentoReserva = LocalDateTime.of(reserva.getFecha(), reserva.getHoraInicio());
        if (momentoReserva.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("No se puede reservar en una fecha u hora que ya ha pasado.");
        }

        List<Reserva> reservasDia = reservaRepository.findByPistaIdAndFechaAndEstado(
            pistaDB.getId(), reserva.getFecha(), EstadoReserva.CONFIRMADA
        );

        boolean solapado = reservasDia.stream().anyMatch(existe ->
            reserva.getHoraInicio().isBefore(existe.getHoraFin()) &&
            reserva.getHoraFin().isAfter(existe.getHoraInicio())
        );

        if (solapado){
            throw new ConflictException("La pista ya está ocupada en ese horario.");
        }
        
        if (reserva.getPrecioPagado() == null || reserva.getPrecioPagado().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Debe especificar un precio pagado válido para la reserva.");
        }

        if (usuarioDB.getSaldo() == null) {
            throw new ConflictException("El usuario no tiene un saldo configurado.");
        }

        if (usuarioDB.getSaldo().compareTo(reserva.getPrecioPagado()) < 0){
            throw new ConflictException("Saldo insuficiente. La pista cuesta " + reserva.getPrecioPagado() 
                    + "€ y tu saldo actual es de " + usuarioDB.getSaldo() + "€.");
        }

        usuarioDB.setSaldo(usuarioDB.getSaldo().subtract(reserva.getPrecioPagado()));
        usuarioRepository.save(usuarioDB);

        reserva.setEstado(EstadoReserva.CONFIRMADA);
        return reservaRepository.save(reserva);
    }

    @Transactional
    public void cancelarReserva(Long id) {
        Reserva reserva = reservaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No se puede cancelar: La reserva con ID " + id + " no existe."));

        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            throw new BadRequestException("La reserva ya se encuentra cancelada.");
        }

        LocalDateTime momentoPartido = LocalDateTime.of(reserva.getFecha(), reserva.getHoraInicio());
        LocalDateTime fechaLimiteReembolsoTotal = momentoPartido.minusHours(24);
        LocalDateTime ahora = LocalDateTime.now();

        BigDecimal importeReembolso = BigDecimal.ZERO;

        if (ahora.isBefore(fechaLimiteReembolsoTotal)) {
            importeReembolso = reserva.getPrecioPagado();
        } else {
            importeReembolso = reserva.getPrecioPagado().divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
        }

        if (importeReembolso.compareTo(BigDecimal.ZERO) > 0) {
            Usuario usuario = reserva.getUsuario();
            if (usuario.getSaldo() != null) {
                usuario.setSaldo(usuario.getSaldo().add(importeReembolso));
                usuarioRepository.save(usuario);
            }
        }

        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);
    }
}