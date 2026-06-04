package com.courtmaster.api.repository;

import com.courtmaster.api.model.Pista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PistaRepository extends JpaRepository<Pista, Long> {
        List<Pista> findByActivaTrue();
}