package com.courtmaster.api.repository;

import com.courtmaster.api.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long>{
    List<Transaccion> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    @Query("SELECT COALESCE(" +
           "SUM(CASE WHEN t.tipoTransaccion = 'RESERVA' THEN t.monto ELSE 0 END) - " +
           "SUM(CASE WHEN t.tipoTransaccion IN ('CANCELACION_TEMPRANA', 'CANCELACION_TARDIA') THEN t.monto ELSE 0 END), 0) " +
           "FROM Transaccion t WHERE t.pista.club.id = :clubId")
    BigDecimal calcularIngresosNetosPorClub(@Param("clubId") Long clubId);

    @Query("SELECT COUNT(t) FROM Transaccion t WHERE t.pista.club.id = :clubId AND t.tipoTransaccion = 'RESERVA'")
    long contarReservasTotalesPorClub(@Param("clubId") Long clubId);
}