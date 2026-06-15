package com.courtmaster.api.controller;

import com.courtmaster.api.dto.ClubDTO;
import com.courtmaster.api.model.Club;
import com.courtmaster.api.service.ClubService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubes")
public class ClubController {

    private final ClubService clubService;

    public ClubController(ClubService clubService) {
        this.clubService = clubService;
    }

    @PostMapping
    public ResponseEntity<ClubDTO> crearClub(@Valid @RequestBody Club club) {
        ClubDTO clubCreado = clubService.crearClub(club);
        return ResponseEntity.status(HttpStatus.CREATED).body(clubCreado);
    }

    @GetMapping
    public ResponseEntity<List<ClubDTO>> listarTodos() {
        List<ClubDTO> clubes = clubService.listarTodos();
        return ResponseEntity.ok(clubes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClubDTO> actualizarClub(@PathVariable Long id, @Valid @RequestBody Club clubDatosNuevos) {
        ClubDTO clubActualizado = clubService.actualizarClub(id, clubDatosNuevos);
        return ResponseEntity.ok(clubActualizado);
    }
}