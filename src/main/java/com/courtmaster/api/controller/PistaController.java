package com.courtmaster.api.controller;

import com.courtmaster.api.model.Pista;
import com.courtmaster.api.service.PistaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;


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

    //POST
    @PostMapping
    public Pista crearPista(@RequestBody Pista pista){
        return pistaService.crearPista(pista);
    }

    //PUT
    @PutMapping("/{id}")
    public Pista actualizarPista(@PathVariable Long id, @RequestBody Pista pista) {
        return pistaService.actualizar(id, pista);
    }

    //DELETE
    @DeleteMapping("/{id}")
    public void desactivar(@PathVariable Long id){
        pistaService.desactivar(id);
    }
}
