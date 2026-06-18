package com.courtmaster.api.service;

import com.courtmaster.api.dto.DashboardReserva;
import com.courtmaster.api.exception.BadRequestException;
import com.courtmaster.api.exception.ResourceNotFoundException;
import com.courtmaster.api.model.Reserva;
import com.courtmaster.api.model.EstadoReserva;
import com.courtmaster.api.model.Usuario;
import com.courtmaster.api.repository.ReservaRepository;
import com.courtmaster.api.repository.TransaccionRepository;
import com.courtmaster.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardOwner {

    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TransaccionRepository transaccionRepository;

    @Transactional(readOnly = true)
    public List<DashboardReserva> obtenerReservasMiClub(Usuario usuarioLogueado) {
        Usuario owner = usuarioRepository.findById(usuarioLogueado.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (owner.getClub() == null) {
            throw new BadRequestException("El usuario actual no tiene ningún club asignado.");
        }

        return reservaRepository.findByPistaClubId(owner.getClub().getId()).stream()
                .map(this::mapperADashboardReserva)
                .toList();
    }

    private DashboardReserva mapperADashboardReserva(Reserva reserva) {
        BigDecimal precioEfectivo = reserva.getPrecioPagado();
        LocalDateTime ahora = LocalDateTime.now();
        
        if (reserva.getEstado() == EstadoReserva.CANCELADA) {
            LocalDateTime momentoPartido = LocalDateTime.of(reserva.getFecha(), reserva.getHoraInicio());
            LocalDateTime limiteReembolso = momentoPartido.minusHours(24);

            if (ahora.isAfter(limiteReembolso)) {
                precioEfectivo = reserva.getPrecioPagado().divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP);
            } else {
                precioEfectivo = BigDecimal.ZERO;
            }
        }

        return DashboardReserva.builder()
                .id(reserva.getId())
                .usuarioEmail(reserva.getUsuario().getEmail())
                .usuarioTelefono(reserva.getUsuario().getTelefono()) 
                .pistaId(reserva.getPista().getId())
                .pistaNombre(reserva.getPista().getNombre())
                .fecha(reserva.getFecha())
                .horaInicio(reserva.getHoraInicio())
                .horaFin(reserva.getHoraFin())
                .precioPagado(precioEfectivo)
                .estado(reserva.getEstado().name())
                .build();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> calcularIngresosMiClub(Usuario usuarioLogueado) {
        Usuario owner = usuarioRepository.findById(usuarioLogueado.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (owner.getClub() == null) {
            throw new BadRequestException("El usuario actual no tiene ningún club asignado.");
        }

        Long clubId = owner.getClub().getId();

        BigDecimal totalIngresos = transaccionRepository.calcularIngresosNetosPorClub(clubId);
        long reservasLiquidadas = transaccionRepository.contarReservasTotalesPorClub(clubId);

        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("clubId", clubId);
        respuesta.put("clubNombre", owner.getClub().getNombre());
        respuesta.put("totalIngresos", totalIngresos);
        respuesta.put("totalReservasLiquidadas", reservasLiquidadas);

        return respuesta;
    }
}