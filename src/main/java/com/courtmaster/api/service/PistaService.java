package com.courtmaster.api.service;

import com.courtmaster.api.model.Club;
import com.courtmaster.api.model.Pista;
import com.courtmaster.api.model.Rol;
import com.courtmaster.api.model.Usuario;
import com.courtmaster.api.repository.ClubRepository;
import com.courtmaster.api.repository.PistaRepository;
import com.courtmaster.api.exception.BadRequestException;
import com.courtmaster.api.exception.ConflictException;
import com.courtmaster.api.exception.ResourceNotFoundException;
import org.springframework.security.access.AccessDeniedException;
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
            throw new BadRequestException("No se puede crear la pista: Debe especificar un Club válido.");
        }
        
        Club clubDB = clubRepository.findById(pista.getClub().getId())
            .orElseThrow(() -> new ResourceNotFoundException("No se puede crear la pista: El Club especificado no existe."));

        pista.setClub(clubDB);

        if (pista.getNombre() == null || pista.getNombre().trim().isEmpty()) {
            throw new BadRequestException("El nombre de la pista no puede estar vacío.");
        }

        boolean existe = pistaRepository.findAll().stream()
            .anyMatch(p -> p.getClub().getId().equals(clubDB.getId()) && 
                           p.getNombre().equalsIgnoreCase(pista.getNombre().trim()));

        if (existe) {
            throw new ConflictException("Ya existe una pista con el nombre: " + pista.getNombre().trim() + " en este club.");
        }

        pista.setNombre(pista.getNombre().trim());
        pista.setActiva(true);
        return pistaRepository.save(pista);
    }

    @Transactional
    public Pista actualizar(Long id, Pista datosActualizados){
        Pista pista = pistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pista no encontrada con id: " + id));

        if (datosActualizados.getNombre() != null && !datosActualizados.getNombre().trim().isEmpty()) {
            String nuevoNombre = datosActualizados.getNombre().trim();
            
            if (!nuevoNombre.equalsIgnoreCase(pista.getNombre())) {
                boolean existe = pistaRepository.findAll().stream()
                        .anyMatch(p -> p.getClub().getId().equals(pista.getClub().getId()) && 
                                       p.getNombre().equalsIgnoreCase(nuevoNombre));
                if (existe) {
                    throw new ConflictException("Ya existe otra pista con el nombre: " + nuevoNombre + " en este club.");
                }
            }
            pista.setNombre(nuevoNombre);
        }

        if (datosActualizados.getTipo() != null) {
            pista.setTipo(datosActualizados.getTipo());
        }
        
        return pistaRepository.save(pista);
    }

    @Transactional
    public void desactivar(Long id, Usuario usuarioLogueado) {
        Pista pista = pistaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede desactivar. Pista no encontrada con id: " + id));

        if (usuarioLogueado.getRol() == Rol.OWNER) {
            if (usuarioLogueado.getClub() == null || !pista.getClub().getId().equals(usuarioLogueado.getClub().getId())) {
                throw new AccessDeniedException("Operación denegada: No tienes permisos para gestionar pistas de otros clubes.");
            }
        }

        pista.setActiva(false);
        pistaRepository.save(pista);
    }
}