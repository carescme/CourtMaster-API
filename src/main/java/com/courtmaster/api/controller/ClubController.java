package com.courtmaster.api.controller;

import com.courtmaster.api.dto.ClubDTO;
import com.courtmaster.api.model.Club;
import com.courtmaster.api.service.ClubService;
import com.courtmaster.api.model.Usuario;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; 
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping("/api/clubes")
public class ClubController {

    private final ClubService clubService;

    public ClubController(ClubService clubService) {
        this.clubService = clubService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClubDTO> crearClub(
            @Valid @RequestBody Club club,
            @RequestParam Long ownerId) { 
        
        ClubDTO clubCreado = clubService.crearClub(club, ownerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(clubCreado);
    }

    @GetMapping
    public ResponseEntity<List<ClubDTO>> listarTodos() {
        List<ClubDTO> clubes = clubService.listarTodos();
        return ResponseEntity.ok(clubes);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')") 
    public ResponseEntity<ClubDTO> actualizarClub(
            @PathVariable Long id, 
            @Valid @RequestBody Club clubDatosNuevos,
            @AuthenticationPrincipal Usuario usuarioLogueado) {
        
        ClubDTO clubActualizado = clubService.actualizarClub(id, clubDatosNuevos, usuarioLogueado);
        return ResponseEntity.ok(clubActualizado);
    }
}