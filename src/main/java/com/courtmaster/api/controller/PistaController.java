package com.courtmaster.api.controller;

import com.courtmaster.api.model.Pista;
import com.courtmaster.api.service.PistaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/pistas")
@RequiredArgsConstructor
public class PistaController {
    private final PistaService pistaService;

    //GET
    // Si pasamos ?soloActivas=true filtrará las disponibles
    @GetMapping
    public List<Pista> listarPistas(@RequestParam(required=false, defaultValue = "false") boolean soloActivas){
        if(soloActivas){
            return pistaService.obtenerActivas();
        }
        return pistaService.obtenerTodas();
    }
}
