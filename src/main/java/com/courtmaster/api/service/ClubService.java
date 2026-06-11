package com.courtmaster.api.service;

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
    public Club crearClub(Club club) {
        // Aquí podrías validar si el email del club ya existe, por ejemplo
        return clubRepository.save(club);
    }

    @Transactional(readOnly = true)
    public List<Club> listarTodos() {
        return clubRepository.findAll();
    }
}