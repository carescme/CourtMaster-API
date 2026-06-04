package com.courtmaster.api.repository;

import com.courtmaster.api.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    
}
