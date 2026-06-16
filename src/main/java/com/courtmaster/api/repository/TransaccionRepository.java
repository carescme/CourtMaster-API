package com.courtmaster.api.repository;

import com.courtmaster.api.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long>{
    List<Transaccion> findByUsuarioIdOrderByFechaDesc(Long usuarioId);
}