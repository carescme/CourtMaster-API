package com.courtmaster.api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.courtmaster.api.model.Usuario;
import com.courtmaster.api.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;

    //GET
    @GetMapping
    public List<Usuario> listaUsuarios(){
        return usuarioService.obtenerTodos();
    }
}
