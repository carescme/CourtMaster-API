package com.courtmaster.api.service;

import com.courtmaster.api.dto.ClubDTO;
import com.courtmaster.api.exception.BadRequestException;
import com.courtmaster.api.exception.ResourceNotFoundException;
import com.courtmaster.api.model.Club;
import com.courtmaster.api.repository.ClubRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class ClubService {

    private final ClubRepository clubRepository;

    public ClubService(ClubRepository clubRepository) {
        this.clubRepository = clubRepository;
    }

    @Transactional
    public ClubDTO crearClub(Club club) {
        if (club.getNombre() == null || club.getNombre().trim().isEmpty()) {
            throw new BadRequestException("El nombre del club no puede estar vacío.");
        }
        if (club.getEmail() == null || club.getEmail().trim().isEmpty()) {
            throw new BadRequestException("El email del club no puede estar vacío.");
        }

        boolean existeEmail = clubRepository.findAll().stream()
                .anyMatch(c -> c.getEmail().equalsIgnoreCase(club.getEmail().trim()));
        if (existeEmail) {
            throw new BadRequestException("Ya existe un club registrado con el email: " + club.getEmail().trim());
        }

        club.setNombre(club.getNombre().trim());
        club.setEmail(club.getEmail().trim());

        Club clubGuardado = clubRepository.save(club);
        return mapperAClubDTO(clubGuardado);
    }

    @Transactional(readOnly = true)
    public List<ClubDTO> listarTodos() {
        return clubRepository.findAll().stream()
                .map(this::mapperAClubDTO)
                .toList();
    }

    @Transactional
    public ClubDTO actualizarClub(Long id, Club clubDatosNuevos) {
        Club clubExistente = clubRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El club con ID " + id + " no existe"));
                
        if (clubDatosNuevos.getNombre() != null && !clubDatosNuevos.getNombre().trim().isEmpty()) {
            clubExistente.setNombre(clubDatosNuevos.getNombre().trim());
        }

        if (clubDatosNuevos.getEmail() != null && !clubDatosNuevos.getEmail().trim().isEmpty()) {
            String nuevoEmail = clubDatosNuevos.getEmail().trim();
            
            if (!nuevoEmail.equalsIgnoreCase(clubExistente.getEmail())) {
                boolean existeEmail = clubRepository.findAll().stream()
                        .anyMatch(c -> !c.getId().equals(id) && c.getEmail().equalsIgnoreCase(nuevoEmail));
                if (existeEmail) {
                    throw new BadRequestException("No se puede actualizar: Ya existe otro club con el email " + nuevoEmail);
                }
            }
            clubExistente.setEmail(nuevoEmail);
        }

        if (clubDatosNuevos.getTelefono() != null) {
            clubExistente.setTelefono(clubDatosNuevos.getTelefono().trim());
        }

        return mapperAClubDTO(clubExistente);
    }

    private ClubDTO mapperAClubDTO(Club club) {
        List<ClubDTO.PistaInfoDTO> pistasDTO = List.of();
        
        if (club.getPistas() != null) {
            pistasDTO = club.getPistas().stream()
                    .<ClubDTO.PistaInfoDTO>map(pista -> ClubDTO.PistaInfoDTO.builder()
                            .id(pista.getId())
                            .nombre(pista.getNombre())
                            .tipo(pista.getTipo())
                            .activa(pista.getActiva()) // Cambiado a getActiva() según tu modelo
                            .build())
                    .toList();
        }

        return ClubDTO.builder()
                .id(club.getId())
                .nombre(club.getNombre())
                .email(club.getEmail())
                .telefono(club.getTelefono())
                .pistas(pistasDTO)
                .build();
    }
}