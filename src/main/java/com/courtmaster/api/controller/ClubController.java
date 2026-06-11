package com.courtmaster.api.controller;

import com.courtmaster.api.model.Club;
import com.courtmaster.api.service.ClubService;
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
    public ResponseEntity<Club> crear(@RequestBody Club club) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clubService.crearClub(club));
    }

    @GetMapping
    public ResponseEntity<List<Club>> listar() {
        return ResponseEntity.ok(clubService.listarTodos());
    }
}