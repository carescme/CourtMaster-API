package com.courtmaster.api.service;

import com.courtmaster.api.dto.ClubDTO;
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
        Club clubGuardado = clubRepository.save(club);
        return mapperAClubDTO(clubGuardado);
    }

    @Transactional(readOnly = true)
    public List<ClubDTO> listarTodos() {
        return clubRepository.findAll().stream()
                .map(this::mapperAClubDTO)
                .toList();
    }

    private ClubDTO mapperAClubDTO(Club club) {
        List<ClubDTO.PistaInfoDTO> pistasDTO = List.of();
        
        if (club.getPistas() != null) {
            pistasDTO = club.getPistas().stream()
                .<ClubDTO.PistaInfoDTO>map(pista -> ClubDTO.PistaInfoDTO.builder() // 🌟 Añadimos el tipo explícito antes de map
                        .id(pista.getId())
                        .nombre(pista.getNombre())
                        .tipo(pista.getTipo())
                        .activa(pista.getActiva())
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

    @Transactional
    public ClubDTO actualizarClub(Long id, Club clubDatosNuevos) {
        Club clubExistente = clubRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("El club con ID " + id + " no existe"));

        clubExistente.setNombre(clubDatosNuevos.getNombre());
        clubExistente.setEmail(clubDatosNuevos.getEmail());
        clubExistente.setTelefono(clubDatosNuevos.getTelefono());

        return mapperAClubDTO(clubExistente);
    }
}