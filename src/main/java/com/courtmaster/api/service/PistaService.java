package com.courtmaster.api.service;

import com.courtmaster.api.model.Pista;
import com.courtmaster.api.repository.PistaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PistaService {
    private final PistaRepository pistaRepository;

    public List<Pista> obtenerTodas(){
        return pistaRepository.findAll();
    }
    
    public List<Pista> obtenerActivas(){
        return pistaRepository.findByActivaTrue();
    }
}
