package com.courtmaster.api.service;

import com.courtmaster.api.model.Club;
import com.courtmaster.api.model.Pista;
import com.courtmaster.api.repository.ClubRepository;
import com.courtmaster.api.repository.PistaRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PistaService {
    private final PistaRepository pistaRepository;
    private final ClubRepository clubRepository;

    @Transactional(readOnly = true)
    public List<Pista> obtenerTodas(){
        return pistaRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Pista> obtenerActivas(){
        return pistaRepository.findByActivaTrue();
    }

    @Transactional
    public Pista crearPista(Pista pista){
        if (pista.getClub() == null || pista.getClub().getId() == null) {
            throw new IllegalArgumentException("No se puede crear la pista: Debe especificar un Club válido.");
        }
        
        Club clubDB = clubRepository.findById(pista.getClub().getId())
            .orElseThrow(() -> new RuntimeException("No se puede crear la pista: El Club especificado no existe."));

        pista.setClub(clubDB);

        if (pista.getNombre() == null || pista.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la pista no puede estar vacío.");
        }

        boolean existe = pistaRepository.findAll().stream()
            .anyMatch(p -> p.getClub().getId().equals(clubDB.getId()) && 
                           p.getNombre().equalsIgnoreCase(pista.getNombre().trim()));

        if (existe) {
            throw new IllegalStateException("Ya existe una pista con el nombre: " + pista.getNombre().trim());
        }

        pista.setNombre(pista.getNombre().trim());
        pista.setActiva(true);
        return pistaRepository.save(pista);
    }

    @Transactional
    public Pista actualizar(Long id, Pista datosActualizados){
        return pistaRepository.findById(id)
            .map(pista -> {
                if (datosActualizados.getNombre() != null && !datosActualizados.getNombre().trim().isEmpty()) {
                    String nuevoNombre = datosActualizados.getNombre().trim();
                    
                    if (!nuevoNombre.equalsIgnoreCase(pista.getNombre())) {
                        boolean existe = pistaRepository.findAll().stream()
                                .anyMatch(p -> p.getNombre().equalsIgnoreCase(nuevoNombre));
                        if (existe) {
                            throw new IllegalStateException("No se puede actualizar: Ya existe otra pista llamada " + nuevoNombre);
                        }
                    }
                    pista.setNombre(nuevoNombre);
                }

                if (datosActualizados.getTipo() != null) {
                    pista.setTipo(datosActualizados.getTipo());
                }
                return pistaRepository.save(pista);
            })
            .orElseThrow(() -> new RuntimeException("Pista no encontrada con id: "+id));
    }

    @Transactional
    public void desactivar(Long id){
        Pista pista = pistaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pista no encontrada con id: "+id));
        pista.setActiva(false);
        pistaRepository.save(pista);
    }
}
