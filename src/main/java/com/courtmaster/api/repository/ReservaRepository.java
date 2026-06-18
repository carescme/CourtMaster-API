package com.courtmaster.api.repository;

import com.courtmaster.api.model.EstadoReserva;
import com.courtmaster.api.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByUsuarioId(Long usuarioId);
    List<Reserva> findByPistaIdAndFechaAndEstado(Long pistaId, LocalDate fecha, EstadoReserva estado);

    List<Reserva> findByPistaClubId(Long clubId);
    List<Reserva> findByPistaClubIdAndEstado(Long clubId, EstadoReserva estado);
}
