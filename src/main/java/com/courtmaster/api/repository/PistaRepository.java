package com.courtmaster.api.repository;

import com.courtmaster.api.model.Pista;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PistaRepository extends JpaRepository<Pista, Long> {
        List<Pista> findByActivaTrue();
}